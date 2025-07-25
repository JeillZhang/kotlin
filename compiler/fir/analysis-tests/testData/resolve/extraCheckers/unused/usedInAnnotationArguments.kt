// RUN_PIPELINE_TILL: FRONTEND
annotation class Ann(val value: Int)

fun foo(): Int {
    val x = 3
    @Ann(<!ANNOTATION_ARGUMENT_MUST_BE_CONST!>x<!>) val y = 5
    return y
}

/* GENERATED_FIR_TAGS: annotationDeclaration, functionDeclaration, integerLiteral, localProperty, primaryConstructor,
propertyDeclaration */
