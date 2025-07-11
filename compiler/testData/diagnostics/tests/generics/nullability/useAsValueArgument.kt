// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER -UNUSED_VARIABLE

fun <T : CharSequence?> bar1(x: T) {}

fun bar2(x: CharSequence?) {}

fun <T : CharSequence> bar3(x: T) {}

fun bar4(x: String) {}

fun <T : String?> foo(x: T) {
    bar1(x)
    bar2(x)

    bar3(<!TYPE_MISMATCH!>x<!>)
    bar4(<!TYPE_MISMATCH!>x<!>)
}

/* GENERATED_FIR_TAGS: functionDeclaration, nullableType, typeConstraint, typeParameter */
