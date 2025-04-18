/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.java.enhancement

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.fir.expressions.explicitTypeArgumentIfMadeFlexibleSynthetically
import org.jetbrains.kotlin.fir.resolve.substitution.AbstractConeSubstitutor
import org.jetbrains.kotlin.fir.types.*
import kotlin.reflect.KClass

data class EnhancedTypeForWarningAttribute(
    override val coneType: ConeKotlinType,
    val isDeprecation: Boolean,
) : ConeAttributeWithConeType<EnhancedTypeForWarningAttribute>() {
    override fun union(other: EnhancedTypeForWarningAttribute?): EnhancedTypeForWarningAttribute? = null
    override fun intersect(other: EnhancedTypeForWarningAttribute?): EnhancedTypeForWarningAttribute? = null
    override fun add(other: EnhancedTypeForWarningAttribute?): EnhancedTypeForWarningAttribute = other ?: this
    override fun isSubtypeOf(other: EnhancedTypeForWarningAttribute?): Boolean = true
    override fun toString(): String = "Enhanced for warning(${coneType.renderForDebugging()})"
    override fun copyWith(newType: ConeKotlinType): EnhancedTypeForWarningAttribute = EnhancedTypeForWarningAttribute(newType, isDeprecation)

    override val key: KClass<out EnhancedTypeForWarningAttribute>
        get() = EnhancedTypeForWarningAttribute::class

    override val keepInInferredDeclarationType: Boolean
        get() = true

    override val implementsEquality: Boolean
        get() = true
}

val ConeAttributes.enhancedTypeForWarning: EnhancedTypeForWarningAttribute? by ConeAttributes.attributeAccessor<EnhancedTypeForWarningAttribute>()

val ConeKotlinType.enhancedTypeForWarning: ConeKotlinType?
    get() = attributes.enhancedTypeForWarning?.coneType

val ConeKotlinType.enhancedTypeForWarningOrSelf: ConeKotlinType
    get() = enhancedTypeForWarning ?: this

val ConeKotlinType.isEnhancedTypeForWarningDeprecation: Boolean
    get() = attributes.enhancedTypeForWarning?.isDeprecation == true

/**
 * Substitutor that substitutes types with their [ConeKotlinType.enhancedTypeForWarning] recursively.
 */
class EnhancedForWarningConeSubstitutor(
    typeContext: ConeTypeContext,
    // TODO: drop me after disabling of DontMakeExplicitJavaTypeArgumentsFlexible will be no longer supported
    // In fact it may be done earlier during the fix of KT-76065 (dropping JavaTypeParameterDefaultRepresentationWithDNN)
    private val useExplicitTypeArgumentIfMadeFlexibleSyntheticallyWithFeature: LanguageFeature? = null,
) : AbstractConeSubstitutor(typeContext) {
    override fun substituteType(type: ConeKotlinType): ConeKotlinType? {
        // We substitute and recombine the bounds of flexible types (using the unsubstituted bounds as fallback) to produce the final type.
        // Examples:
        // (EFW(Any?) Any..Any?) -> coneFlexibleOrSimpleType(Any?, Any?) = Any?
        // (Any..EFW(Any) Any?) -> coneFlexibleOrSimpleType(Any, Any) = Any
        // (EFW(MutableList<>?) MutableList<>..List<>?) -> coneFlexibleOrSimpleType(MutableList<>?, List<>?) -> (MutableList<>?..List<>?)
        if (type is ConeFlexibleType) {
            /**
             * This fast-path enables the optimization for trivial flexible types in [substituteRecursive] where we only substitute one bound.
             * For non-trivial flexible types, we will process both bounds anyway, so we might as well do it here.
             **/
            if (type.isTrivial && type.lowerBound.replacementTopLevelTypeOrNull() == null && type.upperBound.replacementTopLevelTypeOrNull() == null) {
                return null
            }

            val lowerSubstituted = substituteOrNull(type.lowerBound)
            val upperSubstituted = substituteOrNull(type.upperBound)

            if (lowerSubstituted == null && upperSubstituted == null) {
                return null
            }

            return coneFlexibleOrSimpleType(
                typeContext,
                lowerSubstituted ?: type.lowerBound,
                upperSubstituted ?: type.upperBound,
                isTrivial = false
            )
        }

        // If the top-level type can be enhanced, this will only enhance the top-level type but not its arguments: Foo<Bar!>! -> Foo<Bar!>?
        // Otherwise, it will enhance recursively until the first possible enhancement.
        val enhancedTopLevel = type.replacementTopLevelTypeOrNull()

        // This will also enhance type arguments if the top-level type was enhanced, otherwise it will continue enhancing recursively.
        return enhancedTopLevel?.let(::substituteOrSelf)
    }

    private fun ConeKotlinType.replacementTopLevelTypeOrNull(): ConeKotlinType? {
        return enhancedTypeForWarning
            ?: attributes.explicitTypeArgumentIfMadeFlexibleSynthetically
                ?.takeIf { it.relevantFeature == useExplicitTypeArgumentIfMadeFlexibleSyntheticallyWithFeature }
                ?.coneType
    }
}
