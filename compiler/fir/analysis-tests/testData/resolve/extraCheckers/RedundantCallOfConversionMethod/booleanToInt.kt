// RUN_PIPELINE_TILL: BACKEND
// IS_APPLICABLE: false

fun Boolean.toInt() = if (this) 1 else 0

fun test(x: Int, y: Int): Int {
    return (x > y).toInt()
}

/* GENERATED_FIR_TAGS: comparisonExpression, funWithExtensionReceiver, functionDeclaration, ifExpression, integerLiteral,
thisExpression */
