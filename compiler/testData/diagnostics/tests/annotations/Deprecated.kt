// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
import java.lang.Deprecated as deprecated

<!DEPRECATED_JAVA_ANNOTATION!>@java.lang.Deprecated<!> fun foo() {}

<!DEPRECATED_JAVA_ANNOTATION!>@deprecated<!> fun foo1() {}

/* GENERATED_FIR_TAGS: functionDeclaration */
