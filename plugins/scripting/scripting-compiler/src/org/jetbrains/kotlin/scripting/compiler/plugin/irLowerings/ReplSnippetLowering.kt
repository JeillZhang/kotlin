/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package org.jetbrains.kotlin.scripting.compiler.plugin.irLowerings

import org.jetbrains.kotlin.backend.common.ModuleLoweringPass
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.phaser.PhaseDescription
import org.jetbrains.kotlin.backend.jvm.originalSnippetValueSymbol
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities.INTERNAL
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.NameUtils

val REPL_SNIPPET_EVAL_FUN_NAME = Name.identifier("\$\$eval")

@PhaseDescription(name = "ReplSnippetsToClasses")
internal class ReplSnippetsToClassesLowering(val context: IrPluginContext) : ModuleLoweringPass {
    override fun lower(irModule: IrModuleFragment) {
        val snippets = mutableListOf<IrReplSnippet>()

        for (irFile in irModule.files) {
            irFile.declarations.transformInPlace { declaration ->
                if (declaration is IrReplSnippet) {
                    prepareReplSnippetClass(irFile, declaration).also { snippets.add(declaration) }
                } else {
                    declaration
                }
            }
        }

        val symbolRemapper = ReplSnippetsToClassesSymbolRemapper()

        snippets.sortBy { it.name }
        for (irSnippet in snippets) {
            finalizeReplSnippetClass(irSnippet, symbolRemapper)
        }
    }

    private fun prepareReplSnippetClass(irFile: IrFile, irSnippet: IrReplSnippet): IrClass {
        return context.irFactory.buildClass {
            startOffset = irSnippet.startOffset
            endOffset = irSnippet.endOffset
            origin = IrDeclarationOrigin.REPL_SNIPPET_CLASS
            name = NameUtils.getScriptTargetClassName(irSnippet.name)
            kind = ClassKind.OBJECT
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.FINAL
        }.also { irSnippetClass ->
            irSnippetClass.superTypes += context.irBuiltIns.anyType
            irSnippetClass.parent = irFile
            irSnippetClass.metadata = irSnippet.metadata
            irSnippet.targetClass = irSnippetClass.symbol
        }
    }

    private fun collectCapturingClasses(irSnippet: IrReplSnippet, typeRemapper: SimpleTypeRemapper): Set<IrClass> {
        val snippetReceivers = mutableSetOf<IrType>()
        irSnippet.receiverParameters.forEach {
            snippetReceivers.add(it.type)
            snippetReceivers.add(typeRemapper.remapType(it.type))
        }

        return irSnippet.body.statements.filterIsInstance<IrClass>().collectCapturersInScript(
            context, irSnippet, snippetReceivers, emptySet()
        )
    }

    private fun finalizeReplSnippetClass(irSnippet: IrReplSnippet, symbolRemapper: ReplSnippetsToClassesSymbolRemapper) {

        val irSnippetClass = irSnippet.targetClass!!.owner
        val typeRemapper = SimpleTypeRemapper(symbolRemapper)

        val implicitReceiversFieldsWithParameters = makeImplicitReceiversFieldsWithParameters(irSnippetClass, typeRemapper, irSnippet)

        val valsToFields = mutableMapOf<IrVariableSymbol, IrFieldSymbol>()
        val valsToProps = mutableMapOf<IrVariableSymbol, IrPropertySymbol>()

        val irSnippetClassThisReceiver =
            irSnippet.createThisReceiverParameter(context, IrDeclarationOrigin.INSTANCE_RECEIVER, irSnippetClass.typeWith()).also {
                it.parent = irSnippetClass
            }
        irSnippetClass.thisReceiver = irSnippetClassThisReceiver

        val snippetAccessCallsGenerator = ReplSnippetAccessCallsGenerator(
            context, irSnippetClassThisReceiver, implicitReceiversFieldsWithParameters, irSnippetClass, irSnippet.stateObject!!
        )

        irSnippetClass.declarations.add(createConstructor(irSnippetClass))

        irSnippetClass.factory.buildFun {
            name = REPL_SNIPPET_EVAL_FUN_NAME
            startOffset = irSnippet.startOffset
            endOffset = irSnippet.endOffset
            returnType = context.irBuiltIns.unitType
            visibility = INTERNAL
        }.also { evalFun ->
            evalFun.parent = irSnippetClass
            evalFun.parameters = buildList {
                add(
                    evalFun.buildReceiverParameter {
                        origin = irSnippetClass.origin
                        type = irSnippetClass.defaultType
                    }
                )
                implicitReceiversFieldsWithParameters.forEach { (_, param) -> add(param) }
            }
            evalFun.body =
                context.irBuiltIns.createIrBuilder(evalFun.symbol).irBlockBody {
                    +snippetAccessCallsGenerator.createPutSelfToState(
                        ScriptLikeToClassTransformerContext.makeRootContext(
                            valueParameterForScriptThis = evalFun.dispatchReceiverParameter?.symbol,
                            isInScriptConstructor = false,
                            topLevelDeclarationWithScriptReceiver = evalFun
                        )
                    )
                    val flattenedStatements = irSnippet.body.statements.flatMap { snippetStatement ->
                        if (snippetStatement is IrComposite) {
                            snippetStatement.statements
                        } else {
                            listOf(snippetStatement)
                        }
                    }
                    // relies on that Fir2IrVisitor.convertToIrBlockBody insert implicit coercion calls for "unused" statements
                    val lastExpression = (flattenedStatements.lastOrNull() as? IrTypeOperatorCall)?.takeIf {
                        it.operator == IrTypeOperator.IMPLICIT_COERCION_TO_UNIT
                    }
                    var lastExpressionVar: IrVariable? = null
                    flattenedStatements.forEach { statement ->
                        if (statement == lastExpression) {
                            // Could become a `$res..` one
                            lastExpressionVar = createTmpVariable(lastExpression.argument, nameHint = "result")
                        } else {
                            when (statement) {
                                is IrVariable -> {
                                    addPropertyFromLocalVariable(irSnippetClass, statement, irSnippetClassThisReceiver, valsToFields)
                                }
                                is IrLocalDelegatedProperty -> {
                                    addPropertyFromLocalDelegated(irSnippetClass, statement, irSnippetClassThisReceiver, valsToProps)
                                }
                                is IrProperty,
                                is IrSimpleFunction,
                                is IrClass -> {
                                    statement.visibility = DescriptorVisibilities.PUBLIC
                                    statement.parent = irSnippetClass
                                    irSnippetClass.declarations.add(statement)
                                }
                                else -> {
                                    +statement
                                }
                            }
                        }
                    }
                    lastExpression?.let {
                        +irReturn(IrGetValueImpl(it.startOffset, it.endOffset, lastExpressionVar!!.symbol))
                    }
                    evalFun.returnType = lastExpressionVar?.type ?: context.irBuiltIns.unitType
                }
            irSnippetClass.declarations.add(evalFun)
        }

        // required because some declarations deeper in the subtree may get a "wrong" parent on Fir2Ir
        // E.g. anonymous objects in a property initializer. (see KT-75301 for possible future directions).
        // Or lambda as in KT-74607 and KT-77470
        irSnippetClass.declarations.forEach { it.patchDeclarationParents(irSnippet) }

        val scriptTransformer = ReplSnippetToClassTransformer(
            context,
            irSnippet,
            irSnippetClass,
            irSnippetClassThisReceiver,
            typeRemapper,
            snippetAccessCallsGenerator,
            valsToFields,
            valsToProps,
            capturingClasses = collectCapturingClasses(irSnippet, typeRemapper)
        )
        val lambdaPatcher = ScriptFixLambdasTransformer(irSnippetClass)

        irSnippetClass.declarations.transformInPlace {
            val rootContext =
                if (it is IrConstructor)
                    ScriptLikeToClassTransformerContext.makeRootContext(irSnippetClass.thisReceiver!!.symbol, true)
                else
                    ScriptLikeToClassTransformerContext.makeRootContext(null, isInScriptConstructor = false)
            it.transform(scriptTransformer, rootContext)
                .transform(lambdaPatcher, ScriptFixLambdasTransformerContext())
        }

        // TODO: find out what problems could arise from copying annotations applicable to file only (KT-74176)
        irSnippetClass.annotations += (irSnippetClass.parent as IrFile).annotations
    }

    private fun IrBlockBodyBuilder.addPropertyFromLocalVariable(
        irSnippetClass: IrClass,
        statement: IrVariable,
        irSnippetClassThisReceiver: IrValueParameter,
        valsToFields: MutableMap<IrVariableSymbol, IrFieldSymbol>,
    ) {
        irSnippetClass.addProperty {
            updateFrom(statement)
            name = statement.name
        }.also { property ->
            property.backingField = context.irFactory.buildField {
                updateFrom(statement)
                origin = IrDeclarationOrigin.PROPERTY_BACKING_FIELD
                name = statement.name
                type = statement.type
            }.also { field ->
                statement.initializer?.let { initializer ->
                    +IrSetFieldImpl(
                        initializer.startOffset,
                        initializer.endOffset,
                        field.symbol,
                        irGet(irSnippetClassThisReceiver),
                        initializer,
                        this.context.irBuiltIns.unitType
                    )
                }
                field.parent = irSnippetClass
                field.annotations += statement.annotations
                valsToFields[statement.symbol] = field.symbol
            }
            property.addDefaultGetter(irSnippetClass, context.irBuiltIns)
            if (statement.isVar) {
                property.addDefaultSetter(irSnippetClass, context.irBuiltIns)
            }
        }
    }

    private fun IrBlockBodyBuilder.addPropertyFromLocalDelegated(
        irSnippetClass: IrClass,
        statement: IrLocalDelegatedProperty,
        irSnippetClassThisReceiver: IrValueParameter,
        valsToProps: MutableMap<IrVariableSymbol, IrPropertySymbol>,
    ) {
        irSnippetClass.addProperty {
            updateFrom(statement)
            name = statement.name
            isDelegated = true
        }.also { property ->
            property.backingField = context.irFactory.buildField {
                updateFrom(statement.delegate)
                origin = IrDeclarationOrigin.PROPERTY_DELEGATE
                name = statement.delegate.name
                type = statement.delegate.type
                visibility = DescriptorVisibilities.PRIVATE
            }.also { field ->
                statement.delegate.initializer?.let { initializer ->
                    field.initializer = context.irFactory.createExpressionBody(initializer)
                }
                field.parent = irSnippetClass
                field.annotations += statement.annotations
            }
            property.getter = statement.getter.also { getter ->
                getter.parent = irSnippetClass
                getter.correspondingPropertySymbol = property.symbol
            }
            property.setter = statement.setter?.also { setter ->
                setter.parent = irSnippetClass
                setter.correspondingPropertySymbol = property.symbol
            }
            valsToProps[statement.delegate.symbol] = property.symbol
        }
    }

    private fun createConstructor(irSnippetClass: IrClass): IrConstructor =
        irSnippetClass.factory.buildConstructor {
            isPrimary = true
            returnType = irSnippetClass.defaultType
        }.also { irConstructor ->
            irConstructor.body = context.irBuiltIns.createIrBuilder(irConstructor.symbol).irBlockBody {
                +irDelegatingConstructorCall(context.irBuiltIns.anyClass.owner.constructors.single())
                +IrInstanceInitializerCallImpl(startOffset, endOffset, irSnippetClass.symbol, context.irBuiltIns.unitType)
            }
            irConstructor.parent = irSnippetClass
        }
}

private class ReplSnippetsToClassesSymbolRemapper : SymbolRemapper.Empty() {
    override fun getReferencedClassifier(symbol: IrClassifierSymbol): IrClassifierSymbol =
        (symbol.owner as? IrReplSnippet)?.targetClass ?: symbol
}

private class ReplSnippetAccessCallsGenerator(
    context: IrPluginContext,
    snippetClassReceiver: IrValueParameter,
    implicitReceiversFieldsWithParameters: List<Pair<IrField, IrValueParameter>>,
    val irSnippetClass: IrClass,
    val irReplStateObjectSymbol: IrClassSymbol
) : ScriptLikeAccessCallsGenerator(context, snippetClassReceiver, implicitReceiversFieldsWithParameters) {
    private val mapClass = irReplStateObjectSymbol.owner
    private val mapGet = mapClass.functions.single { it.name.asString() == "get" }
    private val mapPut = mapClass.functions.single { it.name.asString() == "put" }

    fun createIrGetValFromState(
        startOffset: Int,
        endOffset: Int,
        irSnippetClassFromState: IrClassSymbol,
        irTargetField: IrFieldSymbol
    ): IrExpression =
        IrGetFieldImpl(
            startOffset,
            endOffset,
            irTargetField,
            irTargetField.owner.type,
            IrStatementOrigin.GET_PROPERTY
        ).also {
            it.receiver =
                createAccessToSnippet(irSnippetClassFromState, startOffset, endOffset)
        }

    fun createIrSetValInState(
        startOffset: Int,
        endOffset: Int,
        irSnippetClassFromState: IrClassSymbol,
        irTargetField: IrFieldSymbol,
        expression: IrExpression
    ): IrExpression =
        IrSetFieldImpl(
            startOffset,
            endOffset,
            irTargetField,
            context.irBuiltIns.unitType,
        ).also {
            it.receiver = createAccessToSnippet(irSnippetClassFromState, startOffset, endOffset)
            it.value = expression
        }

    fun createPutSelfToState(data: ScriptLikeToClassTransformerContext): IrCall =
        IrCallImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, mapPut.returnType, mapPut.symbol).apply {
            arguments[0] =
                IrGetObjectValueImpl(
                    startOffset, endOffset,
                    irReplStateObjectSymbol.typeWith(),
                    irReplStateObjectSymbol,
                )
            arguments[1] =
                IrConstImpl.string(
                    startOffset,
                    endOffset,
                    context.irBuiltIns.stringType,
                    irSnippetClass.name.asString()
                )
            arguments[2] = getAccessCallForSelf(data, startOffset, endOffset, null, null)
        }

    fun createAccessToSnippet(
        irSnippetClassFromState: IrClassSymbol,
        startOffset: Int,
        endOffset: Int,
    ): IrExpression? {
        val getSnippetCall = IrCallImpl(startOffset, endOffset, mapGet.returnType, mapGet.symbol).apply {
            arguments[0] =
                IrGetObjectValueImpl(
                    startOffset, endOffset,
                    irReplStateObjectSymbol.typeWith(),
                    irReplStateObjectSymbol,
                )
            arguments[1] =
                IrConstImpl.string(
                    startOffset,
                    endOffset,
                    context.irBuiltIns.stringType,
                    irSnippetClassFromState.owner.name.asString()
                )
        }
        val targetType = irSnippetClassFromState.typeWith()
        return IrTypeOperatorCallImpl(
            startOffset, endOffset, targetType,
            IrTypeOperator.CAST,
            targetType, getSnippetCall
        )
    }

}

private class ReplSnippetToClassTransformer(
    context: IrPluginContext,
    val irSnippet: IrReplSnippet,
    irSnippetClass: IrClass,
    snippetClassReceiver: IrValueParameter,
    typeRemapper: TypeRemapper,
    override val accessCallsGenerator: ReplSnippetAccessCallsGenerator,
    val varsToFields: Map<IrVariableSymbol, IrFieldSymbol>,
    val varsToProps: Map<IrVariableSymbol, IrPropertySymbol>,
    capturingClasses: Set<IrClass>,
) : ScriptLikeToClassTransformer(
    context,
    irSnippet,
    irSnippetClass,
    snippetClassReceiver,
    typeRemapper,
    accessCallsGenerator,
    capturingClasses,
    needsReceiverProcessing = true
) {
    override fun visitSimpleFunction(
        declaration: IrSimpleFunction,
        data: ScriptLikeToClassTransformerContext,
    ): IrSimpleFunction {
        if (declaration.parent == irSnippet || declaration.parent == irTargetClass) {
            declaration.visibility = DescriptorVisibilities.PUBLIC
        }
        return super.visitSimpleFunction(declaration, data)
    }

    override fun visitGetValue(expression: IrGetValue, data: ScriptLikeToClassTransformerContext): IrExpression {
        val targetPropertyInThisSnippet = varsToProps[expression.symbol]
        if (targetPropertyInThisSnippet != null) {
            return accessCallsGenerator.createIrGetValFromState(
                expression.startOffset, expression.endOffset,
                irTargetClass.symbol,
                targetPropertyInThisSnippet.owner.backingField!!.symbol
            ).also { it.transformChildren(this, data) }
        }

        val targetFieldInThisSnippet = varsToFields[expression.symbol]
        if (targetFieldInThisSnippet != null) {
            return accessCallsGenerator.createIrGetValFromState(
                expression.startOffset, expression.endOffset,
                irTargetClass.symbol,
                targetFieldInThisSnippet
            ).also { it.transformChildren(this, data) }
        }

        val targetFieldFromOtherSnippets =
            irSnippet.variablesFromOtherSnippets.find {
                it.name == expression.symbol.owner.name
            }?.originalSnippetValueSymbol as? IrFieldSymbol
        if (targetFieldFromOtherSnippets != null) {
            return accessCallsGenerator.createIrGetValFromState(
                expression.startOffset, expression.endOffset,
                targetFieldFromOtherSnippets.owner.parentAsClass.symbol,
                targetFieldFromOtherSnippets
            ).also { it.transformChildren(this, data) }
        }
        return super.visitGetValue(expression, data)
    }

    override fun visitSetValue(
        expression: IrSetValue,
        data: ScriptLikeToClassTransformerContext,
    ): IrExpression {
        val targetFieldInThisSnippet = varsToFields[expression.symbol]
        return if (targetFieldInThisSnippet != null) {
            accessCallsGenerator.createIrSetValInState(
                expression.startOffset, expression.endOffset,
                irTargetClass.symbol,
                targetFieldInThisSnippet,
                expression.value
            ).also { it.transformChildren(this, data) }
        } else {
            val targetFieldFromOtherSnippets =
                irSnippet.variablesFromOtherSnippets.find {
                    it.name == expression.symbol.owner.name
                }?.originalSnippetValueSymbol as? IrFieldSymbol
            if (targetFieldFromOtherSnippets != null) {
                accessCallsGenerator.createIrSetValInState(
                    expression.startOffset, expression.endOffset,
                    targetFieldFromOtherSnippets.owner.parentAsClass.symbol,
                    targetFieldFromOtherSnippets,
                    expression.value
                ).also { it.transformChildren(this, data) }
            } else {
                super.visitSetValue(expression, data)
            }
        }
    }

    override fun visitLocalDelegatedPropertyReference(
        expression: IrLocalDelegatedPropertyReference,
        data: ScriptLikeToClassTransformerContext
    ): IrElement {
        val targetPropertyInThisSnippet = varsToProps[expression.delegate]
        if (targetPropertyInThisSnippet != null) {
            return IrPropertyReferenceImpl(
                expression.startOffset, expression.endOffset,
                expression.type,
                targetPropertyInThisSnippet,
                0,
                targetPropertyInThisSnippet.owner.backingField?.symbol,
                targetPropertyInThisSnippet.owner.getter?.symbol,
                targetPropertyInThisSnippet.owner.setter?.symbol,
                expression.origin
            )
        }
        return super.visitLocalDelegatedPropertyReference(expression, data)
    }

    override fun visitConstructorCall(
        expression: IrConstructorCall,
        data: ScriptLikeToClassTransformerContext,
    ): IrElement {
        return if ((expression.symbol.owner.parent as? IrDeclaration)?.let { it in irSnippet.declarationsFromOtherSnippets } == true) {
            expression.arguments +=
                accessCallsGenerator.createAccessToSnippet(
                    ((expression.symbol.owner.parent as IrClass).parent as IrClass).symbol,
                    expression.startOffset, expression.endOffset
                )
            expression.transformChildren(this, data)
            expression
        } else {
            super.visitConstructorCall(expression, data)
        }
    }

    override fun visitClass(declaration: IrClass, data: ScriptLikeToClassTransformerContext): IrClass {
        declaration.updateVisibilityToPublicIfNeeded()
        return super.visitClass(declaration, data)
    }

    override fun visitFunction(declaration: IrFunction, data: ScriptLikeToClassTransformerContext): IrStatement {
        declaration.updateVisibilityToPublicIfNeeded()
        return super.visitFunction(declaration, data)
    }

    override fun visitProperty(declaration: IrProperty, data: ScriptLikeToClassTransformerContext): IrStatement {
        declaration.updateVisibilityToPublicIfNeeded()
        return super.visitProperty(declaration, data)
    }
}

private fun IrDeclarationWithVisibility.updateVisibilityToPublicIfNeeded() {
    // The snippet top-level classes visibilities are set to public, so this function is used to update
    // visibilities of such class memebrs recursively, to avoid incorrect codegeneration
    if (visibility == DescriptorVisibilities.LOCAL &&
        parent.let { it is IrClass && it.visibility == DescriptorVisibilities.PUBLIC }
    ) {
        visibility = DescriptorVisibilities.PUBLIC
    }
}

private fun makeImplicitReceiversFieldsWithParameters(
    irSnippetClass: IrClass,
    typeRemapper: SimpleTypeRemapper,
    irSnippet: IrReplSnippet,
): List<Pair<IrField, IrValueParameter>> =
    irSnippet.receiverParameters.map { param ->
        val typeName = param.type.classFqName?.shortName()?.identifierOrNullIfSpecial
        irSnippetClass.addField {
            startOffset = UNDEFINED_OFFSET
            endOffset = UNDEFINED_OFFSET
            origin = IrDeclarationOrigin.SCRIPT_IMPLICIT_RECEIVER
            name = Name.identifier("\$\$implicitReceiver_${typeName ?: param.indexInParameters.toString()}")
            visibility = DescriptorVisibilities.PRIVATE
            type = typeRemapper.remapType(param.type)
            isFinal = true
        } to param
    }