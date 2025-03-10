/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/fir/tree/tree-generator/Readme.md.
// DO NOT MODIFY IT MANUALLY.

@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlin.fir.references.impl

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.diagnostics.ConeDiagnostic
import org.jetbrains.kotlin.fir.references.FirThisReference
import org.jetbrains.kotlin.fir.symbols.impl.FirThisOwnerSymbol
import org.jetbrains.kotlin.fir.visitors.FirTransformer
import org.jetbrains.kotlin.fir.visitors.FirVisitor

internal class FirExplicitThisReference(
    override val source: KtSourceElement?,
    override val labelName: String?,
    override var diagnostic: ConeDiagnostic?,
) : FirThisReference() {
    override var boundSymbol: FirThisOwnerSymbol<*>? = null
    override val isImplicit: Boolean = false

    override fun <R, D> acceptChildren(visitor: FirVisitor<R, D>, data: D) {}

    override fun <D> transformChildren(transformer: FirTransformer<D>, data: D): FirExplicitThisReference {
        return this
    }

    override fun replaceBoundSymbol(newBoundSymbol: FirThisOwnerSymbol<*>?) {
        boundSymbol = newBoundSymbol
    }

    override fun replaceDiagnostic(newDiagnostic: ConeDiagnostic?) {
        diagnostic = newDiagnostic
    }
}
