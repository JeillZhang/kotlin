// RUN_PIPELINE_TILL: BACKEND
// LANGUAGE: +MultiPlatformProjects
// MODULE: common

expect class Test

expect val Test.<!EXTENSION_SHADOWED_BY_MEMBER{JVM}!>number<!>: Int

// MODULE: jvm()()(common)

actual class Test {
  val number = 10
}

actual val Test.<!EXTENSION_SHADOWED_BY_MEMBER!>number<!> get() = this.number

// MODULE: js()()(common)

actual class Test

actual val Test.number get() = 20

/* GENERATED_FIR_TAGS: actual, classDeclaration, expect, getter, integerLiteral, propertyDeclaration,
propertyWithExtensionReceiver, thisExpression */
