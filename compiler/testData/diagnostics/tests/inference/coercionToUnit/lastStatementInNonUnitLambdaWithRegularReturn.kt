// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-74478

fun foo(x: () -> String) {}

fun bar(a: MutableList<String>, b: Boolean) {
    foo {
        if (b) return@foo ""
        <!ASSIGNMENT_TYPE_MISMATCH!>a[0] = ""<!> // should be an error here
    }
}

/* GENERATED_FIR_TAGS: assignment, functionDeclaration, functionalType, ifExpression, integerLiteral, lambdaLiteral,
stringLiteral */
