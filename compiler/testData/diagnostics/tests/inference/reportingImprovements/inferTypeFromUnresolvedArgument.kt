// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

fun <K> id2(x: K, s: String): K = x
fun <K> ret(s: String): K = TODO()

fun test() {
    id2(<!UNRESOLVED_REFERENCE!>unresolved<!>, "foo")
    id2(<!UNRESOLVED_REFERENCE!>unresolved<!>, <!CONSTANT_EXPECTED_TYPE_MISMATCH!>42<!>)

    <!NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>ret<!>("foo")
    ret(<!CONSTANT_EXPECTED_TYPE_MISMATCH!>42<!>)
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, nullableType, stringLiteral, typeParameter */
