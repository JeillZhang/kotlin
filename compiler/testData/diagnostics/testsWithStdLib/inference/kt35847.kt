// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// SKIP_TXT
// DIAGNOSTICS: -UNUSED_PARAMETER

import kotlin.reflect.KProperty1
import kotlin.reflect.KFunction1

fun <T, R> has(property: KFunction1<T, R>) = null
fun <T, R> has(property: KProperty1<T, R>) = null

fun toInt(s: String) = 10

object A {
    fun main() {
        has(::toInt) // throwing an exception here
    }
}

/* GENERATED_FIR_TAGS: callableReference, functionDeclaration, integerLiteral, nullableType, objectDeclaration,
typeParameter */
