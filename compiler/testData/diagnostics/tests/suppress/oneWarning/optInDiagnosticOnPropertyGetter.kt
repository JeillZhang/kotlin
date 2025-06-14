// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -ERROR_SUPPRESSION
// WITH_STDLIB
// ISSUE: KT-66258

@RequiresOptIn
annotation class Ann

class A(
    @get:Ann
    @Suppress("OPT_IN_MARKER_ON_WRONG_TARGET")
    val s: String
)

class B {
    @get:Ann
    @Suppress("OPT_IN_MARKER_ON_WRONG_TARGET")
    val s: String = ""
}

/* GENERATED_FIR_TAGS: annotationDeclaration, annotationUseSiteTargetPropertyGetter, classDeclaration,
primaryConstructor, propertyDeclaration, stringLiteral */
