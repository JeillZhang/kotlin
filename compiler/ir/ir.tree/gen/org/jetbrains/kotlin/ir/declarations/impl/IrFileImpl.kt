/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

@file:Suppress("DuplicatedCode")

package org.jetbrains.kotlin.ir.declarations.impl

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.MetadataSource
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrFileSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.name.FqName

class IrFileImpl(
    override var fileEntry: IrFileEntry,
    override val symbol: IrFileSymbol,
    override var packageFqName: FqName,
) : IrFile() {
    override var startOffset: Int
        get() = 0
        set(value) {
            error("Mutation of startOffset is not supported for this class.")
        }

    override var endOffset: Int
        get() = fileEntry.maxOffset
        set(value) {
            error("Mutation of endOffset is not supported for this class.")
        }

    override var attributeOwnerId: IrElement = this

    @UnsafeDuringIrConstructionAPI
    override val declarations: MutableList<IrDeclaration> = ArrayList()

    override var annotations: List<IrConstructorCall> = emptyList()

    override var metadata: MetadataSource? = null

    override lateinit var module: IrModuleFragment

    init {
        symbol.bind(this)
    }
}
