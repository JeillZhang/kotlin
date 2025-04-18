/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.transformers

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.KtRealSourceElementKind
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.computeTypeAttributes
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.diagnostics.ConeDiagnostic
import org.jetbrains.kotlin.fir.diagnostics.ConeSimpleDiagnostic
import org.jetbrains.kotlin.fir.diagnostics.ConeUnexpectedTypeArgumentsError
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.*
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeTypeVisibilityError
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeUnresolvedTypeQualifierError
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeUnsupportedDefaultValueInFunctionType
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeVisibilityError
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.resultType
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.builder.buildErrorTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildUserTypeRef
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.util.PrivateForInline

class FirSpecificTypeResolverTransformer(
    override val session: FirSession,
    private val errorTypeAsResolved: Boolean = true,
    private val resolveDeprecations: Boolean = true,
    private val supertypeSupplier: SupertypeSupplier = SupertypeSupplier.Default,
    private val expandTypeAliases: Boolean,
) : FirAbstractTreeTransformer<TypeResolutionConfiguration>(phase = FirResolvePhase.SUPER_TYPES) {
    private val typeResolver get() = session.typeResolver

    @set:PrivateForInline
    var areBareTypesAllowed: Boolean = false

    @OptIn(PrivateForInline::class)
    inline fun <R> withBareTypes(allowed: Boolean = true, block: () -> R): R {
        val oldValue = areBareTypesAllowed
        areBareTypesAllowed = allowed
        return try {
            block()
        } finally {
            areBareTypesAllowed = oldValue
        }
    }

    @set:PrivateForInline
    var isOperandOfIsOperator: Boolean = false

    @OptIn(PrivateForInline::class)
    inline fun <R> withIsOperandOfIsOperator(block: () -> R): R {
        val oldValue = isOperandOfIsOperator
        isOperandOfIsOperator = true
        return try {
            block()
        } finally {
            isOperandOfIsOperator = oldValue
        }
    }

    @OptIn(PrivateForInline::class)
    override fun transformTypeRef(typeRef: FirTypeRef, data: TypeResolutionConfiguration): FirResolvedTypeRef {
        withBareTypes(allowed = false) {
            typeRef.transformChildren(this, data)
        }
        val (resolvedType, diagnostic) = resolveType(typeRef, data, expandTypeAliases)
        return transformType(typeRef, resolvedType, diagnostic, data)
    }

    @OptIn(PrivateForInline::class)
    override fun transformFunctionTypeRef(
        functionTypeRef: FirFunctionTypeRef,
        data: TypeResolutionConfiguration
    ): FirResolvedTypeRef {
        functionTypeRef.transformChildren(this, data)
        val resolvedTypeWithDiagnostic = resolveType(functionTypeRef, data)
        val resolvedType = resolvedTypeWithDiagnostic.type.takeIfAcceptable()
        val diagnostic = resolvedTypeWithDiagnostic.diagnostic
        return if (resolvedType != null && resolvedType !is ConeErrorType && diagnostic == null) {
            buildResolvedTypeRef {
                source = functionTypeRef.source
                coneType = resolvedType
                annotations += functionTypeRef.annotations
                delegatedTypeRef = functionTypeRef
            }
        } else {
            buildErrorTypeRef {
                source = functionTypeRef.source
                if (resolvedType != null) {
                    coneType = resolvedType
                }
                annotations += functionTypeRef.annotations
                this.diagnostic = diagnostic ?: (resolvedType as? ConeErrorType)?.diagnostic
                        ?: ConeSimpleDiagnostic("Unresolved function type: ${functionTypeRef.render()}")
            }
        }
    }

    @OptIn(PrivateForInline::class)
    private fun FirSpecificTypeResolverTransformer.resolveType(
        typeRef: FirTypeRef,
        configuration: TypeResolutionConfiguration,
        expandTypeAliases: Boolean = true,
    ): FirTypeResolutionResult {
        return typeResolver.resolveType(
            typeRef,
            configuration,
            areBareTypesAllowed,
            isOperandOfIsOperator,
            resolveDeprecations,
            supertypeSupplier,
            expandTypeAliases,
        )
    }

    private fun transformType(
        typeRef: FirTypeRef,
        resolvedType: ConeKotlinType,
        diagnostic: ConeDiagnostic?,
        configuration: TypeResolutionConfiguration,
    ): FirResolvedTypeRef {
        return when {
            resolvedType is ConeErrorType -> {
                buildErrorType(typeRef, resolvedType, resolvedType.diagnostic, configuration)
            }
            diagnostic != null -> {
                buildErrorType(typeRef, resolvedType, diagnostic, configuration)
            }
            else -> {
                buildResolvedTypeRef {
                    source = typeRef.source
                    coneType = resolvedType
                    annotations += typeRef.annotations
                    delegatedTypeRef = typeRef
                }
            }
        }
    }

    private fun buildErrorType(
        typeRef: FirTypeRef,
        resolvedType: ConeKotlinType,
        diagnostic: ConeDiagnostic,
        configuration: TypeResolutionConfiguration,
    ): FirErrorTypeRef {
        return buildErrorTypeRef {
            val typeRefSourceKind = typeRef.source?.kind
            val diagnosticSource = (diagnostic as? ConeUnexpectedTypeArgumentsError)?.source

            source = if (diagnosticSource != null) {
                if (typeRefSourceKind is KtFakeSourceElementKind) {
                    diagnosticSource.fakeElement(typeRefSourceKind)
                } else {
                    diagnosticSource
                }
            } else {
                typeRef.source
            }?.fakeIfAbbreviated(resolvedType)

            delegatedTypeRef = typeRef
            coneType = resolvedType
            annotations += typeRef.annotations

            val partiallyResolvedTypeRef = tryCalculatingPartiallyResolvedTypeRef(typeRef, configuration)
            this.partiallyResolvedTypeRef = partiallyResolvedTypeRef

            this.diagnostic = when {
                diagnostic is ConeUnresolvedTypeQualifierError -> {
                    ConeUnresolvedTypeQualifierError(smallestUnresolvablePrefix(diagnostic.qualifiers, partiallyResolvedTypeRef))
                }
                diagnostic is ConeVisibilityError && typeRef is FirUserTypeRef -> {
                    ConeTypeVisibilityError(diagnostic.symbol, smallestUnresolvablePrefix(typeRef.qualifier, partiallyResolvedTypeRef))
                }
                else -> diagnostic
            }
        }
    }

    /**
     * We don't want to report errors from typealiases' expanded type refs once again
     * per every use site, but we should remember that some errors on types with abbreviations
     * are caused by the use site (e.g. `INVISIBLE_REFERENCE`), so those must not be ignored.
     */
    private fun KtSourceElement.fakeIfAbbreviated(type: ConeKotlinType): KtSourceElement =
        takeUnless { kind is KtRealSourceElementKind && type.abbreviatedType?.isTypealiasWithErrorInExpansion == true }
            ?: fakeElement(KtFakeSourceElementKind.ErroneousTypealiasExpansion)

    private val ConeKotlinType.isTypealiasWithErrorInExpansion: Boolean
        get() = toTypeAliasSymbol(session)?.resolvedExpandedTypeRef is FirErrorTypeRef

    /**
     * Returns the smallest non-resolvable prefix of the given [qualifiers].
     *
     * Examples:
     *
     * - Given `A.B.C` and `A.B` can be resolved, then `A.B.C` will be returned
     * - Given `A.B.C` and `A` cannot be resolved, then `A` will be returned
     * - Given `a.b.C` and package `a` exists but package `a.b` doesn't exist, `a.b.` will be returned.
     */
    private fun smallestUnresolvablePrefix(
        qualifiers: List<FirQualifierPart>,
        partiallyResolvedTypeRef: FirResolvedTypeRef?,
    ): List<FirQualifierPart> {
        val totalQualifierCount = qualifiers.size
        val resolvedQualifierCount = (partiallyResolvedTypeRef?.delegatedTypeRef as? FirUserTypeRef)?.qualifier?.size
            ?: calculatePartiallyResolvablePackageSegments(qualifiers)

        val unresolvedQualifierCount = totalQualifierCount - resolvedQualifierCount

        return if (unresolvedQualifierCount > 1) {
            qualifiers.dropLast(unresolvedQualifierCount - 1)
        } else {
            qualifiers
        }
    }


    /**
     * Tries to calculate a partially resolved type reference for a type reference which was resolved to an error type.
     * It will attempt to resolve the type with a decreasing number of qualifiers until it succeeds, allowing
     * partial resolution in case of errors in the type reference.
     *
     * This is useful for providing better IDE support when resolving partially incorrect types.
     *
     * When trying to resolve the qualifying types, we use [withBareTypes] to ignore the required type arguments in them,
     * because a type qualifier might not need them at all (e.g. `Map` in `Map.Entry<...>`).
     *
     * @param typeRef The type reference for which to try to calculate a partially resolved type reference.
     * @param data The scope class declaration containing relevant information for resolving the reference.
     * @return A partially resolved type reference if it was resolved, or `null` otherwise.
     */
    private fun tryCalculatingPartiallyResolvedTypeRef(typeRef: FirTypeRef, data: TypeResolutionConfiguration): FirResolvedTypeRef? {
        if (typeRef !is FirUserTypeRef) return null
        val qualifiers = typeRef.qualifier
        if (qualifiers.size <= 1) {
            return null
        }
        val qualifiersToTry = qualifiers.toMutableList()
        while (qualifiersToTry.size > 1) {
            qualifiersToTry.removeLast()
            val typeRefToTry = buildUserTypeRef {
                qualifier += qualifiersToTry
                isMarkedNullable = false
                source = typeRef.source
            }

            val (resolvedType, diagnostic) = withBareTypes { resolveType(typeRefToTry, data) }
            if (resolvedType is ConeErrorType || diagnostic != null) continue

            return buildResolvedTypeRef {
                source = qualifiersToTry.last().source
                coneType = resolvedType
                delegatedTypeRef = typeRefToTry
            }
        }
        return null
    }


    /**
     * If the given [qualifiers] are interpreted as a fully qualified name,
     * calculates how many segments (from the left) can be resolved to an existing package.
     *
     * This is useful for providing better IDE support when resolving partially incorrect types.
     *
     * The last segment is never considered, i.e., if [qualifiers] is not empty, the result is always `< qualifiers.size`.
     */
    private fun calculatePartiallyResolvablePackageSegments(qualifiers: List<FirQualifierPart>): Int {
        if (qualifiers.size <= 1) {
            return 0
        }

        val packageSegmentsToTry = qualifiers.mapTo(mutableListOf()) { it.name.asString() }

        while (packageSegmentsToTry.size > 1) {
            packageSegmentsToTry.removeLast()
            if (session.symbolProvider.hasPackage(FqName.fromSegments(packageSegmentsToTry))) {
                return packageSegmentsToTry.size
            }
        }

        return 0
    }

    private fun ConeKotlinType.takeIfAcceptable(): ConeKotlinType? = this.takeUnless {
        !errorTypeAsResolved && it is ConeErrorType
    }

    override fun transformResolvedTypeRef(resolvedTypeRef: FirResolvedTypeRef, data: TypeResolutionConfiguration): FirTypeRef {
        return resolvedTypeRef
    }

    override fun transformErrorTypeRef(errorTypeRef: FirErrorTypeRef, data: TypeResolutionConfiguration): FirTypeRef {
        errorTypeRef.transformPartiallyResolvedTypeRef(this, data)
        if (errorTypeRef.annotations.isEmpty() || errorTypeRef.coneType.attributes.isNotEmpty()) {
            return errorTypeRef
        }
        // Otherwise we "lose" annotation arguments here, and later cannot resolve them
        return errorTypeRef.withReplacedConeType(
            errorTypeRef.coneType.withAttributes(
                errorTypeRef.annotations.computeTypeAttributes(session, shouldExpandTypeAliases = true)
            )
        )
    }

    override fun transformImplicitTypeRef(implicitTypeRef: FirImplicitTypeRef, data: TypeResolutionConfiguration): FirTypeRef {
        return implicitTypeRef
    }

    override fun transformValueParameter(valueParameter: FirValueParameter, data: TypeResolutionConfiguration): FirStatement {
        val result = transformElement(valueParameter, data)
        result.defaultValue?.let {
            it.resultType = ConeErrorType(ConeUnsupportedDefaultValueInFunctionType(it.source))
        }
        return result
    }
}
