// RUN_PIPELINE_TILL: FRONTEND
class C<<!REIFIED_TYPE_PARAMETER_NO_INLINE!>reified<!> T>

fun <T> id(p: T): T = p

fun <A> main() {
    <!CANNOT_INFER_PARAMETER_TYPE!>C<!>()

    val a: C<A> = <!TYPE_PARAMETER_AS_REIFIED!>C<!>()
    C<<!TYPE_PARAMETER_AS_REIFIED!>A<!>>()

    val b: C<Int> = C()
    C<Int>()

    // TODO svtk, uncomment when extensions are called for nested calls!
    //val < !UNUSED_VARIABLE!>с< !>: C<A> = id(< !TYPE_PARAMETER_AS_REIFIED!>C< !>())
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, localProperty, nullableType, propertyDeclaration, reified,
typeParameter */
