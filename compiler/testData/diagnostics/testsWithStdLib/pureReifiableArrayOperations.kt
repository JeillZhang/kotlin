// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_VARIABLE

fun test() {
    arrayOf<List<String>>(listOf(""))
    arrayOf(listOf(""))

    arrayOf<Array<String>>(arrayOf(""))
    arrayOf(arrayOf(""))

    arrayOfNulls<Array<String>>(1)

    Array<Array<String>>(1) { arrayOf("") }
    Array(1) { arrayOf("") }
    Array(1) { arrayOf("") }

    emptyArray<Array<String>>()
    val x: Array<Array<String>> = emptyArray()
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, lambdaLiteral, localProperty, nullableType,
propertyDeclaration, stringLiteral */
