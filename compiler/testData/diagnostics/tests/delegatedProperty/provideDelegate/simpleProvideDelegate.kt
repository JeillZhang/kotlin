// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER

import kotlin.reflect.KProperty

operator fun String.provideDelegate(a: Any?, p: KProperty<*>) = this
operator fun String.getValue(a: Any?, p: KProperty<*>) = this

val test1: String by "OK"

val test2 by "OK"

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, nullableType, operator, propertyDeclaration,
propertyDelegate, starProjection, stringLiteral, thisExpression */
