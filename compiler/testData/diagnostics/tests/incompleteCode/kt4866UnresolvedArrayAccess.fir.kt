// RUN_PIPELINE_TILL: FRONTEND
//KT-4866 Resolve does not work inside brackets with unresolved reference before

fun test(i: Int, j: Int) {
    <!UNRESOLVED_REFERENCE!>foo<!><!NO_GET_METHOD!>[i, j]<!>
}

/* GENERATED_FIR_TAGS: functionDeclaration */
