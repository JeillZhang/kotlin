// RUN_PIPELINE_TILL: FRONTEND
open class A {
    protected fun foo() {}

    init {
        B.foo() // Ok, receiver (B.Companion) is subtype of A
        (B.Companion).foo()
    }
}

class B {
    companion object : A()
}

class C: A() {
    init {
        B.<!INVISIBLE_MEMBER!>foo<!>() // Error: receiver is not suitable
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, companionObject, functionDeclaration, init, objectDeclaration */
