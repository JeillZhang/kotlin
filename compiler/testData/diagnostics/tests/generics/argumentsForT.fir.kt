// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

fun <T> foo(t: T<!TYPE_ARGUMENTS_NOT_ALLOWED!><String, Int><!>) {}

interface A
class B<T: A>
fun <T> foo1(t: T<!TYPE_ARGUMENTS_NOT_ALLOWED!><B<String>><!>) {}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, nullableType, typeConstraint,
typeParameter */
