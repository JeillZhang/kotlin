// RUN_PIPELINE_TILL: BACKEND
// WITH_STDLIB
// ISSUE: KT-20617

fun Int.function() = run {
    this@function + 1
}

val Int.property1: Int
    get() {
        return run { this@property1 + 1 }
    }

val Int.property2 get() = run {
    this@property2 + 1
}

/* GENERATED_FIR_TAGS: additiveExpression, funWithExtensionReceiver, functionDeclaration, getter, integerLiteral,
lambdaLiteral, propertyDeclaration, propertyWithExtensionReceiver, thisExpression */
