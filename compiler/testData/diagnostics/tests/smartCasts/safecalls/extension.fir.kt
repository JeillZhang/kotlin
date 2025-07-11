// RUN_PIPELINE_TILL: BACKEND
fun String.foo(arg: Int) = this[arg]

fun calc(x: String?) {
    // x should be non-null in arguments list
    x?.foo(x.length - 1)
}

/* GENERATED_FIR_TAGS: additiveExpression, funWithExtensionReceiver, functionDeclaration, integerLiteral, nullableType,
safeCall, smartcast, thisExpression */
