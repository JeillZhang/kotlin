// RUN_PIPELINE_TILL: FRONTEND
// JET-11 Redeclaration & Forward reference for classes cause an exception
open class <!PACKAGE_OR_CLASSIFIER_REDECLARATION!>NoC<!>
class NoC1 : NoC()
open class <!PACKAGE_OR_CLASSIFIER_REDECLARATION!>NoC<!>

/* GENERATED_FIR_TAGS: classDeclaration */
