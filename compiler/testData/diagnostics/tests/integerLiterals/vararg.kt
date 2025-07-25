// RUN_PIPELINE_TILL: BACKEND
// FIR_DUMP

fun <A : Comparable<A>> arrayData(vararg values: A): A = null!!

fun <A> arrayDataNoBound(vararg values: A): A = null!!

fun test(b: Byte) {
    select(<!TYPE_MISMATCH!>arrayData(1)<!>, b)
    select(<!TYPE_MISMATCH!>id(1)<!>, b)
    select(<!TYPE_MISMATCH!>id(arrayData(1))<!>, b)
    select(arrayDataNoBound(1), b)
}

fun <S> select(a: S, b: S) = a

fun <I : Comparable<I>> id(arg: I) = arg

/* GENERATED_FIR_TAGS: checkNotNullCall, functionDeclaration, nullableType, typeConstraint, typeParameter, vararg */
