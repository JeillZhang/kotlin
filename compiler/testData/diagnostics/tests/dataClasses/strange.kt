// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// LANGUAGE: +DataObjects

<!WRONG_MODIFIER_TARGET!>data<!> enum class First(val x: Int) {
    A(1),
    B(2)
}

data object Second

<!WRONG_MODIFIER_TARGET!>data<!> interface Third

<!WRONG_MODIFIER_TARGET!>data<!> annotation class Fourth(val x: Int)

/* GENERATED_FIR_TAGS: annotationDeclaration, data, enumDeclaration, enumEntry, interfaceDeclaration, objectDeclaration,
primaryConstructor, propertyDeclaration */
