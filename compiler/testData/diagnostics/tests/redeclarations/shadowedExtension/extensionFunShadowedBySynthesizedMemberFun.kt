// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER

data class DataClass(val x: Int)

fun DataClass.<!EXTENSION_SHADOWED_BY_MEMBER!>component1<!>() = 42

/* GENERATED_FIR_TAGS: classDeclaration, data, funWithExtensionReceiver, functionDeclaration, integerLiteral,
primaryConstructor, propertyDeclaration */
