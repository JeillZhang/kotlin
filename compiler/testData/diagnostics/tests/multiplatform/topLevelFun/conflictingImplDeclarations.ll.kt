// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: FIR2IR
// MODULE: m1-common
// FILE: common.kt

expect fun foo()

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

actual <!CONFLICTING_OVERLOADS!>fun foo()<!> {}
actual <!CONFLICTING_OVERLOADS!>fun foo()<!> {}

/* GENERATED_FIR_TAGS: actual, expect, functionDeclaration */
