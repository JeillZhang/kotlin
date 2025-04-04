/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:kotlin.internal.JvmBuiltin

package kotlin

/**
 * The type with only one value: the `Unit` object. This type corresponds to the `void` type in Java.
 */
public actual object Unit {
    override fun toString(): String = "kotlin.Unit"
}
