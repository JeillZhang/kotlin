// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// SKIP_TXT

typealias A = CharSequence.(Int) -> Unit

var w: Int = 1

fun myPrint(x: Int) {}

val a1: A = when (w) {
    1 -> { a: Int -> myPrint(a + this.length + 1) }
    else -> { a: Int -> myPrint(a + this.length + 2) }
}

val a2: A = try {
    { a: Int -> myPrint(a + this.length + 1) }
} catch (t: Throwable) {
    { a: Int -> myPrint(a + this.length + 2) }
}

/* GENERATED_FIR_TAGS: additiveExpression, equalityExpression, functionDeclaration, functionalType, integerLiteral,
lambdaLiteral, localProperty, propertyDeclaration, thisExpression, tryExpression, typeAliasDeclaration,
typeWithExtension, whenExpression, whenWithSubject */
