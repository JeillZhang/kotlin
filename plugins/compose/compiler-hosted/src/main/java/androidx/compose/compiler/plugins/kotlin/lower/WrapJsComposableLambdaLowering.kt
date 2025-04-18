/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package androidx.compose.compiler.plugins.kotlin.lower

import androidx.compose.compiler.plugins.kotlin.ComposeCallableIds
import androidx.compose.compiler.plugins.kotlin.ComposeClassIds
import androidx.compose.compiler.plugins.kotlin.FeatureFlags
import androidx.compose.compiler.plugins.kotlin.ModuleMetrics
import androidx.compose.compiler.plugins.kotlin.analysis.StabilityInferencer
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrImplementationDetail
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionReferenceImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.utils.IDEAPluginsCompatibilityAPI

/**
 * This lowering is necessary for k/js:
 * `composableLambda` and `composableLambdaInstance` return an instance of ComposableLambda which
 * doesn't implement FunctionX interfaces in k/js (due to k/js limitation), therefore they can't be
 * invoked using Function.invoke symbol.
 *
 * This lowering finds all calls to `composableLambda(...)` and `composableLambdaInstance(...)`,
 * then transforms them into lambda expressions wrapping the original ComposableLambda instance:
 *
 * composableLambda Before the lowering:
 * ``` composableLambda(...) ```
 * After the lowering:
 * ```
 * run<FunctionX> { // same type as lambda argument in composableLambda call
 *    val composableLambdaVar = composableLambda(...) // the original call
 *    // return the same instance if composableLambdaVar didn't change
 *    return@run remember(composableLambdaVar) { composableLambdaVar::invoke }
 * }
 * ```
 * composableLambdaInstance After the lowering:
 * ```
 * composableLambdaInstance()::invoke
 * ```
 */
class WrapJsComposableLambdaLowering(
    context: IrPluginContext,
    metrics: ModuleMetrics,
    stabilityInferencer: StabilityInferencer,
    featureFlags: FeatureFlags,
) : AbstractComposeLowering(
    context,
    metrics,
    stabilityInferencer,
    featureFlags,
) {
    private val rememberFunSymbol by lazy {
        val composerParamTransformer = ComposerParamTransformer(
            context, stabilityInferencer, metrics, featureFlags
        )
        getTopLevelFunctions(ComposeCallableIds.remember)
            .map { it.owner }
            .first {
                it.parameters.size == 2 && !it.parameters.first().isVararg
            }.symbol.owner
            .let {
                composerParamTransformer.visitSimpleFunction(it) as IrSimpleFunction
            }.symbol
    }

    /**
     * Compatibility check:
     * ComposableLambda in compose-runtime for k/wasm used to not inherit from kotlin.Function2, Function3, etc. types.
     * This lowering must run when the module is linked with such an older compose-runtime.
     * If the linked compose-runtime version is a newer one (where ComposableLambda inherits from kotlin.Function types),
     * this lowering might be skipped to avoid redundant code generation.
     */
    private fun shouldSkipLowering(): Boolean {
        val function2 = context.irBuiltIns.functionN(2)
        val skipLowering = getTopLevelClass(ComposeClassIds.ComposableLambda).owner.superTypes.any {
            it.classOrNull == function2
        }
        return skipLowering
    }

    override fun lower(irModule: IrModuleFragment) {
        if (shouldSkipLowering()) return
        irModule.transformChildrenVoid(this)
        irModule.patchDeclarationParents()
    }

    override fun visitCall(expression: IrCall): IrExpression {
        val original = super.visitCall(expression) as IrCall
        return when (expression.symbol.owner.fqNameForIrSerialization) {
            ComposeCallableIds.composableLambda.asSingleFqName() -> {
                transformComposableLambdaCall(
                    originalCall = original.deepCopyWithoutPatchingParents(), // To avoid duplicated IR nodes, since we reuse the call's args
                    currentComposer = original.arguments.first(),
                    lambda = original.arguments.last() as IrFunctionExpression
                )
            }
            ComposeCallableIds.rememberComposableLambda.asSingleFqName() -> {
                transformComposableLambdaCall(
                    originalCall = original.deepCopyWithoutPatchingParents(), // To avoid duplicated IR nodes, since we reuse the call's args
                    currentComposer = original.arguments[3],
                    lambda = original.arguments[2] as IrFunctionExpression
                )
            }
            ComposeCallableIds.composableLambdaInstance.asSingleFqName() -> {
                transformComposableLambdaInstanceCall(original)
            }
            else -> original
        }
    }

    private fun functionReferenceForComposableLambda(
        lambda: IrFunctionExpression,
        dispatchReceiver: IrExpression,
    ): IrFunctionReferenceImpl {
        val argumentsCount = lambda.function.parameters.size

        val invokeSymbol = getTopLevelClass(ComposeClassIds.ComposableLambda)
            .functions.single {
                it.owner.name.asString() == "invoke" &&
                        argumentsCount == it.owner.parameters.count { it.kind != IrParameterKind.DispatchReceiver }
            }

        return IrFunctionReferenceImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = lambda.type,
            symbol = invokeSymbol,
            typeArgumentsCount = invokeSymbol.owner.typeParameters.size
        ).also { reference ->
            reference.dispatchReceiver = dispatchReceiver
        }
    }

    private fun transformComposableLambdaCall(
        originalCall: IrCall,
        currentComposer: IrExpression?,
        lambda: IrFunctionExpression,
    ): IrExpression {
        val composableLambdaVar = irTemporary(originalCall, "dispatchReceiver")
        // create dispatchReceiver::invoke function reference
        val funReference = functionReferenceForComposableLambda(
            lambda, irGet(composableLambdaVar)
        )

        val calculationFunSymbol = IrSimpleFunctionSymbolImpl()
        val rememberBlock = createLambda0(
            returnType = lambda.type,
            functionSymbol = calculationFunSymbol,
            statements = listOf(irReturn(calculationFunSymbol, funReference))
        )

        // create remember(dispatchReceiver,...) { dispatchReceiver::invoke }
        val rememberCall = IrCallImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = lambda.type,
            symbol = rememberFunSymbol,
            typeArgumentsCount = 1
        ).apply {
            typeArguments[0] = lambda.type
            arguments[0] = irGet(composableLambdaVar) // key1
            arguments[1] = rememberBlock // calculation
            arguments[2] = currentComposer // composer
            arguments[3] = irConst(0) // changed
        }

        val runBlockSymbol = IrSimpleFunctionSymbolImpl()
        val runBlock = createLambda0(
            returnType = lambda.type,
            functionSymbol = runBlockSymbol,
            statements = mutableListOf<IrStatement>().apply {
                add(composableLambdaVar) // referenced in rememberCall
                add(irReturn(runBlockSymbol, rememberCall))
            }
        )

        return callRun(returnType = lambda.type, runBlock = runBlock)
    }

    private fun transformComposableLambdaInstanceCall(originalCall: IrCall): IrExpression {
        val lambda = originalCall.arguments.last() as IrFunctionExpression

        // create composableLambdaInstance::invoke function reference
        return functionReferenceForComposableLambda(lambda, originalCall)
    }

    private fun callRun(returnType: IrType, runBlock: IrFunctionExpressionImpl): IrCall {
        val runSymbol = getTopLevelFunction(
            CallableId(FqName("kotlin"), Name.identifier("run"))
        )
        return IrCallImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = returnType,
            symbol = runSymbol,
            typeArgumentsCount = 1
        ).apply {
            typeArguments[0] = returnType
            arguments[0] = runBlock
        }
    }

    @OptIn(IrImplementationDetail::class, IDEAPluginsCompatibilityAPI::class)
    private fun createLambda0(
        returnType: IrType,
        functionSymbol: IrSimpleFunctionSymbol = IrSimpleFunctionSymbolImpl(),
        statements: List<IrStatement>,
    ): IrFunctionExpressionImpl {
        return IrFunctionExpressionImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = context.irBuiltIns.functionN(0).typeWith(returnType),
            origin = IrStatementOrigin.LAMBDA,
            function = context.irFactory.createSimpleFunction(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA,
                symbol = functionSymbol,
                name = SpecialNames.ANONYMOUS,
                visibility = DescriptorVisibilities.LOCAL,
                modality = Modality.FINAL,
                returnType = returnType,
                isInline = true,
                isExternal = false,
                isTailrec = false,
                isSuspend = false,
                isOperator = false,
                isInfix = false,
                isExpect = false
            ).apply {
                body = context.irFactory.createBlockBody(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET).apply {
                    this.statements.addAll(statements)
                }
            }
        )
    }
}
