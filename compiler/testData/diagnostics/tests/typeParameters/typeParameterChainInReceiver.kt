// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// ISSUE: KT-56212
fun <F> F.<!UNRESOLVED_REFERENCE!>X<!>.f(): Boolean = false

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, nullableType, typeParameter */
