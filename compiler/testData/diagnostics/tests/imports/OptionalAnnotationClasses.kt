// RUN_PIPELINE_TILL: BACKEND
// WITH_STDLIB

@file:Suppress("OPTIONAL_DECLARATION_USAGE_IN_NON_COMMON_SOURCE")

import kotlin.js.*
import kotlin.native.concurrent.*

@JsName("")
public fun test() {}

@ThreadLocal
private val EmptyArray: Array<Int> = arrayOf()

/* GENERATED_FIR_TAGS: annotationUseSiteTargetFile, functionDeclaration, propertyDeclaration, stringLiteral */
