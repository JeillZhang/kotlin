/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.components

import org.jetbrains.kotlin.analysis.api.KaContextParameterApi
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.analysis.api.lifetime.withValidityAssertion
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaTypeAliasSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.builtins.functions.FunctionTypeKind
import org.jetbrains.kotlin.name.ClassId

@SubclassOptInRequired(KaImplementationDetail::class)
public interface KaTypeInformationProvider : KaSessionComponent {
    /**
     * Whether the [KaType] is denotable. A [denotable type](https://kotlinlang.org/spec/type-system.html#type-kinds) can be expressed in
     * Kotlin code, as opposed to being only constructible via compiler type operations (such as type inference).
     */
    public val KaType.isDenotable: Boolean

    /**
     * Whether the [KaType] is a [functional interface type](https://kotlinlang.org/docs/fun-interfaces.html), such as [Runnable]. Such
     * types are also known as SAM types.
     */
    public val KaType.isFunctionalInterface: Boolean

    /**
     * The [FunctionTypeKind] of the given [KaType], or `null` if the type is not a function type.
     */
    @KaExperimentalApi
    public val KaType.functionTypeKind: FunctionTypeKind?

    /**
     * Whether the [KaType] is a [kotlin.Function] type.
     */
    @OptIn(KaExperimentalApi::class)
    public val KaType.isFunctionType: Boolean
        get() = withValidityAssertion { functionTypeKind == FunctionTypeKind.Function }

    /**
     * Whether the [KaType] is a [kotlin.reflect.KFunction] type.
     */
    @OptIn(KaExperimentalApi::class)
    public val KaType.isKFunctionType: Boolean
        get() = withValidityAssertion { functionTypeKind == FunctionTypeKind.KFunction }

    /**
     * Whether the [KaType] is a [suspend function](https://kotlinlang.org/spec/asynchronous-programming-with-coroutines.html#suspending-functions)
     * type.
     */
    @OptIn(KaExperimentalApi::class)
    public val KaType.isSuspendFunctionType: Boolean
        get() = withValidityAssertion { functionTypeKind == FunctionTypeKind.SuspendFunction }

    /**
     * Whether the [KaType] is a `KSuspendFunction` type.
     */
    @OptIn(KaExperimentalApi::class)
    public val KaType.isKSuspendFunctionType: Boolean
        get() = withValidityAssertion { functionTypeKind == FunctionTypeKind.KSuspendFunction }

    /**
     * Whether a public value of the [KaType] can potentially be `null`.
     *
     * If a type can be `null`, it means that this type is not a subtype of [Any]. However, it does not mean one can assign `null` to a
     * variable of this type. It may be unknown whether this type can accept `null`.
     *
     * #### Example
     *
     * A public value of type `T : Any?` can potentially be `null`. But one cannot assign `null` to such a variable because the instantiated
     * type may not be nullable.
     */
    @Deprecated("Use `isNullable` instead", ReplaceWith("this.isNullable"))
    public val KaType.canBeNull: Boolean
        get() = isNullable

    /**
     * Whether a public value of the [KaType] can potentially be `null`.
     *
     * If a type can be `null`, it means that this type is not a subtype of [Any]. However, it does not mean one can assign `null` to a
     * variable of this type. It may be unknown whether this type can accept `null`.
     *
     * #### Example
     *
     * A public value of type `T : Any?` can potentially be `null`. But one cannot assign `null` to such a variable because the instantiated
     * type may not be nullable.
     */
    public val KaType.isNullable: Boolean

    /**
     * Whether the [KaType] is explicitly marked as nullable, i.e., is represented as `T?`.
     *
     * Note that this property just reflects the presence of nullability in the type signature,
     * and sometimes [isMarkedNullable] being false doesn't imply that the given type cannot hold `null` or be assigned with it.
     *
     * For example, [isMarkedNullable] doesn't expand type aliases to check the nullability of their underlying type:
     * ```kotlin
     * typealias NonMarkedNullableAlias = String?
     *
     * fun main() {
     *     val x: NonMarkedNullableAlias = null
     * }
     * ```
     * The type of `x` is `NonMarkedNullableAlias`, which is not marked as nullable. However, it still represents a nullable type and can hold `null` and can be assigned with that.
     *
     * To explicitly check whether a type can potentially hold `null`, use [isNullable].
     */
    public val KaType.isMarkedNullable: Boolean

    /**
     * Whether the [KaType] is a [org.jetbrains.kotlin.analysis.api.types.KaFlexibleType] / [org.jetbrains.kotlin.analysis.api.types.KaDynamicType] with flexible nullability or [org.jetbrains.kotlin.analysis.api.types.KaErrorType] with unknown nullability.
     * Both safe and ordinary calls are valid on such types.
     *
     * Note that a flexible / dynamic type has a flexible nullability when the lower bound is non-nullable and the upper bound is nullable.
     * E.g. `T!` has `T` as the lower bound and `T?` as the upper bound, hence it has a flexible nullability.
     */
    public val KaType.hasFlexibleNullability: Boolean

    /**
     * Whether the [KaType] is a [Unit] type.
     */
    public val KaType.isUnitType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.UNIT) }

    /**
     * Whether the [KaType] is an [Int] type.
     */
    public val KaType.isIntType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.INT) }

    /**
     * Whether the [KaType] is a [Long] type.
     */
    public val KaType.isLongType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.LONG) }

    /**
     * Whether the [KaType] is a [Short] type.
     */
    public val KaType.isShortType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.SHORT) }

    /**
     * Whether the [KaType] is a [Byte] type.
     */
    public val KaType.isByteType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.BYTE) }

    /**
     * Whether the [KaType] is a [Float] type.
     */
    public val KaType.isFloatType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.FLOAT) }

    /**
     * Whether the [KaType] is a [Double] type.
     */
    public val KaType.isDoubleType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.DOUBLE) }

    /**
     * Whether the [KaType] is a [Char] type.
     */
    public val KaType.isCharType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.CHAR) }

    /**
     * Whether the [KaType] is a [Boolean] type.
     */
    public val KaType.isBooleanType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.BOOLEAN) }

    /**
     * Whether the [KaType] is a [String] type.
     */
    public val KaType.isStringType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.STRING) }

    /**
     * Whether the [KaType] is a [CharSequence] type.
     */
    public val KaType.isCharSequenceType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.CHAR_SEQUENCE) }

    /**
     * Whether the [KaType] is an [Any] type.
     */
    public val KaType.isAnyType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.ANY) }

    /**
     * Whether the [KaType] is a [Nothing] type.
     */
    public val KaType.isNothingType: Boolean get() = withValidityAssertion { isClassType(DefaultTypeClassIds.NOTHING) }

    /**
     * Whether the [KaType] is a [UInt] type.
     */
    public val KaType.isUIntType: Boolean get() = withValidityAssertion { isClassType(StandardNames.FqNames.uInt) }

    /**
     * Whether the [KaType] is a [ULong] type.
     */
    public val KaType.isULongType: Boolean get() = withValidityAssertion { isClassType(StandardNames.FqNames.uLong) }

    /**
     * Whether the [KaType] is a [UShort] type.
     */
    public val KaType.isUShortType: Boolean get() = withValidityAssertion { isClassType(StandardNames.FqNames.uShort) }

    /**
     * Whether the [KaType] is a [UByte] type.
     */
    public val KaType.isUByteType: Boolean get() = withValidityAssertion { isClassType(StandardNames.FqNames.uByte) }

    /**
     * The class symbol backing the given [KaType], if available.
     */
    public val KaType.expandedSymbol: KaClassSymbol?
        get() = withValidityAssertion {
            return when (this) {
                is KaClassType -> when (val symbol = symbol) {
                    is KaClassSymbol -> symbol
                    is KaTypeAliasSymbol -> symbol.expandedType.expandedSymbol
                }
                else -> null
            }
        }

    /**
     * The type that corresponds to the given [KaType] with fully expanded type aliases.
     *
     * Type aliases are usually expanded immediately by the compiler, so most [KaType]s should already present in their expanded forms.
     * Nonetheless, it is possible to obtain unexpanded types from the Analysis API, and [fullyExpandedType] may be used to expand type
     * aliases in such types.
     *
     * #### Example
     *
     * ```kotlin
     * interface Base
     *
     * typealias FirstAlias = @Anno1 Base
     * typealias SecondAlias = @Anno2 FirstAlias
     *
     * fun foo(): @Anno3 SecondAlias = TODO()
     * ```
     *
     * The return type of `foo()` will be `@Anno3 @Anno2 @Anno1 Base` instead of `@Anno3 SecondAlias`
     *
     * @see KaType.abbreviation
     */
    public val KaType.fullyExpandedType: KaType

    /**
     * Whether the [KaType] is an array or a primitive array type.
     */
    public val KaType.isArrayOrPrimitiveArray: Boolean

    /**
     * Whether the [KaType] is an array or a primitive array type, and its element is also an array type.
     */
    public val KaType.isNestedArray: Boolean

    /**
     * Checks whether the given [KaType] is a class type with the given [ClassId].
     */
    public fun KaType.isClassType(classId: ClassId): Boolean = withValidityAssertion {
        if (this !is KaClassType) return false
        return this.classId == classId
    }

    /**
     * Whether the [KaType] is a primitive type.
     */
    public val KaType.isPrimitive: Boolean
        get() = withValidityAssertion {
            if (this !is KaClassType) return false
            return this.classId in DefaultTypeClassIds.PRIMITIVES
        }

    /**
     * The default initializer for the given [KaType], or `null` if the type is neither nullable, a primitive, nor a string.
     */
    @KaExperimentalApi
    public val KaType.defaultInitializer: String?
        get() = withValidityAssertion {
            when {
                isMarkedNullable -> "null"
                isIntType || isLongType || isShortType || isByteType -> "0"
                isFloatType -> "0.0f"
                isDoubleType -> "0.0"
                isCharType -> "'\\u0000'"
                isBooleanType -> "false"
                isUnitType -> "Unit"
                isStringType -> "\"\""
                isUIntType -> "0.toUInt()"
                isULongType -> "0.toULong()"
                isUShortType -> "0.toUShort()"
                isUByteType -> "0.toUByte()"
                else -> null
            }
        }
}

public object DefaultTypeClassIds {
    /** The [Unit] class ID. */
    public val UNIT: ClassId = ClassId.topLevel(StandardNames.FqNames.unit.toSafe())

    /** The [Int] class ID. */
    public val INT: ClassId = ClassId.topLevel(StandardNames.FqNames._int.toSafe())

    /** The [Long] class ID. */
    public val LONG: ClassId = ClassId.topLevel(StandardNames.FqNames._long.toSafe())

    /** The [Short] class ID. */
    public val SHORT: ClassId = ClassId.topLevel(StandardNames.FqNames._short.toSafe())

    /** The [Byte] class ID. */
    public val BYTE: ClassId = ClassId.topLevel(StandardNames.FqNames._byte.toSafe())

    /** The [Float] class ID. */
    public val FLOAT: ClassId = ClassId.topLevel(StandardNames.FqNames._float.toSafe())

    /** The [Double] class ID. */
    public val DOUBLE: ClassId = ClassId.topLevel(StandardNames.FqNames._double.toSafe())

    /** The [Char] class ID. */
    public val CHAR: ClassId = ClassId.topLevel(StandardNames.FqNames._char.toSafe())

    /** The [Boolean] class ID. */
    public val BOOLEAN: ClassId = ClassId.topLevel(StandardNames.FqNames._boolean.toSafe())

    /** The [String] class ID. */
    public val STRING: ClassId = ClassId.topLevel(StandardNames.FqNames.string.toSafe())

    /** The [CharSequence] class ID. */
    public val CHAR_SEQUENCE: ClassId = ClassId.topLevel(StandardNames.FqNames.charSequence.toSafe())

    /** The [Any] class ID. */
    public val ANY: ClassId = ClassId.topLevel(StandardNames.FqNames.any.toSafe())

    /** The [Nothing] class ID. */
    public val NOTHING: ClassId = ClassId.topLevel(StandardNames.FqNames.nothing.toSafe())

    /** A set of primitive class IDs. */
    public val PRIMITIVES: Set<ClassId> = setOf(INT, LONG, SHORT, BYTE, FLOAT, DOUBLE, CHAR, BOOLEAN)
}

/**
 * @see KaTypeInformationProvider.isDenotable
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isDenotable: Boolean
    get() = with(context) { isDenotable }

/**
 * @see KaTypeInformationProvider.isFunctionalInterface
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isFunctionalInterface: Boolean
    get() = with(context) { isFunctionalInterface }

/**
 * @see KaTypeInformationProvider.functionTypeKind
 */
@KaExperimentalApi
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.functionTypeKind: FunctionTypeKind?
    get() = with(context) { functionTypeKind }

/**
 * @see KaTypeInformationProvider.isFunctionType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isFunctionType: Boolean
    get() = with(context) { isFunctionType }

/**
 * @see KaTypeInformationProvider.isKFunctionType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isKFunctionType: Boolean
    get() = with(context) { isKFunctionType }

/**
 * @see KaTypeInformationProvider.isSuspendFunctionType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isSuspendFunctionType: Boolean
    get() = with(context) { isSuspendFunctionType }

/**
 * @see KaTypeInformationProvider.isKSuspendFunctionType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isKSuspendFunctionType: Boolean
    get() = with(context) { isKSuspendFunctionType }

/**
 * @see KaTypeInformationProvider.isNullable
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isNullable: Boolean
    get() = with(context) { isNullable }

/**
 * @see KaTypeInformationProvider.isMarkedNullable
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isMarkedNullable: Boolean
    get() = with(context) { isMarkedNullable }

/**
 * @see KaTypeInformationProvider.hasFlexibleNullability
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.hasFlexibleNullability: Boolean
    get() = with(context) { hasFlexibleNullability }

/**
 * @see KaTypeInformationProvider.isUnitType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isUnitType: Boolean
    get() = with(context) { isUnitType }

/**
 * @see KaTypeInformationProvider.isIntType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isIntType: Boolean
    get() = with(context) { isIntType }

/**
 * @see KaTypeInformationProvider.isLongType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isLongType: Boolean
    get() = with(context) { isLongType }

/**
 * @see KaTypeInformationProvider.isShortType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isShortType: Boolean
    get() = with(context) { isShortType }

/**
 * @see KaTypeInformationProvider.isByteType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isByteType: Boolean
    get() = with(context) { isByteType }

/**
 * @see KaTypeInformationProvider.isFloatType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isFloatType: Boolean
    get() = with(context) { isFloatType }

/**
 * @see KaTypeInformationProvider.isDoubleType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isDoubleType: Boolean
    get() = with(context) { isDoubleType }

/**
 * @see KaTypeInformationProvider.isCharType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isCharType: Boolean
    get() = with(context) { isCharType }

/**
 * @see KaTypeInformationProvider.isBooleanType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isBooleanType: Boolean
    get() = with(context) { isBooleanType }

/**
 * @see KaTypeInformationProvider.isStringType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isStringType: Boolean
    get() = with(context) { isStringType }

/**
 * @see KaTypeInformationProvider.isCharSequenceType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isCharSequenceType: Boolean
    get() = with(context) { isCharSequenceType }

/**
 * @see KaTypeInformationProvider.isAnyType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isAnyType: Boolean
    get() = with(context) { isAnyType }

/**
 * @see KaTypeInformationProvider.isNothingType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isNothingType: Boolean
    get() = with(context) { isNothingType }

/**
 * @see KaTypeInformationProvider.isUIntType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isUIntType: Boolean
    get() = with(context) { isUIntType }

/**
 * @see KaTypeInformationProvider.isULongType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isULongType: Boolean
    get() = with(context) { isULongType }

/**
 * @see KaTypeInformationProvider.isUShortType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isUShortType: Boolean
    get() = with(context) { isUShortType }

/**
 * @see KaTypeInformationProvider.isUByteType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isUByteType: Boolean
    get() = with(context) { isUByteType }

/**
 * @see KaTypeInformationProvider.expandedSymbol
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.expandedSymbol: KaClassSymbol?
    get() = with(context) { expandedSymbol }

/**
 * @see KaTypeInformationProvider.fullyExpandedType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.fullyExpandedType: KaType
    get() = with(context) { fullyExpandedType }

/**
 * @see KaTypeInformationProvider.isArrayOrPrimitiveArray
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isArrayOrPrimitiveArray: Boolean
    get() = with(context) { isArrayOrPrimitiveArray }

/**
 * @see KaTypeInformationProvider.isNestedArray
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isNestedArray: Boolean
    get() = with(context) { isNestedArray }

/**
 * @see KaTypeInformationProvider.isClassType
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public fun KaType.isClassType(classId: ClassId): Boolean {
    return with(context) { isClassType(classId) }
}

/**
 * @see KaTypeInformationProvider.isPrimitive
 */
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.isPrimitive: Boolean
    get() = with(context) { isPrimitive }

/**
 * @see KaTypeInformationProvider.defaultInitializer
 */
@KaExperimentalApi
@KaContextParameterApi
context(context: KaTypeInformationProvider)
public val KaType.defaultInitializer: String?
    get() = with(context) { defaultInitializer }
