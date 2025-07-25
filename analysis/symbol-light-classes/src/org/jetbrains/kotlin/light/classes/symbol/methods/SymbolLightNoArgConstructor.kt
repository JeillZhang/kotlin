/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.light.classes.symbol.methods

import com.intellij.psi.*
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KaSymbolPointer
import org.jetbrains.kotlin.asJava.builder.LightMemberOrigin
import org.jetbrains.kotlin.asJava.classes.lazyPub
import org.jetbrains.kotlin.asJava.elements.KtLightIdentifier
import org.jetbrains.kotlin.light.classes.symbol.annotations.EmptyAnnotationsBox
import org.jetbrains.kotlin.light.classes.symbol.annotations.GranularAnnotationsBox
import org.jetbrains.kotlin.light.classes.symbol.annotations.JvmExposeBoxedAdditionalAnnotationsProvider
import org.jetbrains.kotlin.light.classes.symbol.annotations.SymbolAnnotationsProvider
import org.jetbrains.kotlin.light.classes.symbol.classes.SymbolLightClassBase
import org.jetbrains.kotlin.light.classes.symbol.modifierLists.InitializedModifiersBox
import org.jetbrains.kotlin.light.classes.symbol.modifierLists.SymbolLightMemberModifierList
import org.jetbrains.kotlin.light.classes.symbol.parameters.SymbolLightParameterList

internal class SymbolLightNoArgConstructor(
    lightMemberOrigin: LightMemberOrigin?,
    containingClass: SymbolLightClassBase,
    private val visibility: String,
    methodIndex: Int,
    isJvmExposedBoxed: Boolean,
    private val functionSymbolPointer: KaSymbolPointer<KaConstructorSymbol>? = null,
) : SymbolLightMethodBase(
    lightMemberOrigin = lightMemberOrigin,
    containingClass = containingClass,
    methodIndex = methodIndex,
    isJvmExposedBoxed = isJvmExposedBoxed,
) {
    override fun getName(): String = containingClass.name ?: ""

    override fun isConstructor(): Boolean = true

    override fun hasTypeParameters(): Boolean = false
    override fun isVarArgs(): Boolean = false
    override fun getTypeParameterList(): PsiTypeParameterList? = null
    override fun getTypeParameters(): Array<PsiTypeParameter> = PsiTypeParameter.EMPTY_ARRAY

    override fun getNameIdentifier(): PsiIdentifier = KtLightIdentifier(this, ktDeclaration = null)

    override fun isDeprecated(): Boolean = false

    private val _modifierList: PsiModifierList by lazyPub {
        SymbolLightMemberModifierList(
            containingDeclaration = this,
            modifiersBox = InitializedModifiersBox(visibility),
            annotationsBox = if (functionSymbolPointer == null) {
                EmptyAnnotationsBox
            } else {
                GranularAnnotationsBox(
                    annotationsProvider = SymbolAnnotationsProvider(
                        ktModule = ktModule,
                        annotatedSymbolPointer = functionSymbolPointer,
                    ),
                    annotationFilter = jvmExposeBoxedAwareAnnotationFilter,
                    additionalAnnotationsProvider = JvmExposeBoxedAdditionalAnnotationsProvider,
                )
            }
        )
    }

    override fun getModifierList(): PsiModifierList = _modifierList

    private val _parameterList: PsiParameterList by lazyPub {
        SymbolLightParameterList(parent = this)
    }

    override fun getParameterList(): PsiParameterList = _parameterList

    override fun getReturnType(): PsiType? = null

    override fun equals(other: Any?): Boolean =
        this === other || other is SymbolLightNoArgConstructor &&
                isJvmExposedBoxed == other.isJvmExposedBoxed &&
                containingClass == other.containingClass

    override fun hashCode(): Int = containingClass.hashCode()

    override fun isValid(): Boolean = super.isValid() && containingClass.isValid

    override fun isOverride(): Boolean = false
}
