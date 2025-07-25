// RUN_PIPELINE_TILL: BACKEND
// FIR_DUMP
// ISSUE: KT-55804

fun foo() {
    val x: String?
    x = materialize()!! // Should be treated as non-nullable assignment
    <!DEBUG_INFO_SMARTCAST!>x<!>.length // Should be allowed
}

fun <E> materialize(): E = TODO()

fun <F> test(f: F) = f!!

fun main() {
    test<String>("").length
    test<String?>(null).length // `.length` should be allowed because return type of "test" should be inferred to `F & Any`
}

/* GENERATED_FIR_TAGS: assignment, checkNotNullCall, dnnType, functionDeclaration, localProperty, nullableType,
propertyDeclaration, smartcast, stringLiteral, typeParameter */
