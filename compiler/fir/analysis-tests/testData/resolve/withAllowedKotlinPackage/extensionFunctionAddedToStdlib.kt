// RUN_PIPELINE_TILL: FRONTEND
// FILE: main.kt
package test

class TestClass {
    fun test() {
        <!UNRESOLVED_REFERENCE!>extensionFun<!>()
    }
}

// FILE: stdlibExt.kt
package kotlin

class MyClass

fun MyClass.extensionFun() {}

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration */
