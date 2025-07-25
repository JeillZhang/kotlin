// RUN_PIPELINE_TILL: BACKEND
package o

class A {
    infix fun foo(b: B) = b
}

class B {
    fun bar() {}
}

fun test(a: A, b: B?) {
    a foo b!!
    b.bar()
}

/* GENERATED_FIR_TAGS: checkNotNullCall, classDeclaration, functionDeclaration, infix, nullableType, smartcast */
