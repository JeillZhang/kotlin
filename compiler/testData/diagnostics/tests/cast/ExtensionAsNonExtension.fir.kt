// RUN_PIPELINE_TILL: BACKEND
fun f(a: (Int) -> Unit) {
    a <!USELESS_CAST!>as Int.() -> Unit<!>

    f1(a <!USELESS_CAST!>as Int.() -> Unit<!>)
}

fun f1(a: Int.() -> Unit) {
    a <!USELESS_CAST!>as (Int) -> Unit<!>
    f(a <!USELESS_CAST!>as (Int) -> Unit<!>)
}

/* GENERATED_FIR_TAGS: asExpression, functionDeclaration, functionalType, typeWithExtension */
