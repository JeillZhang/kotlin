// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
fun foo(init: A.() -> Unit) { }

class A {
    var x: Int = 0
    companion object {
        val f = foo() {
            x = 1
        }
    }
}

class B(val a: String) {
    fun f() = 0
    companion object   {
        fun B.bar() = a + f()
    }
}

open class C {
    fun bar() {}
}

class E: C() {

    class D {
        init {
            with(C()) {
                bar()
                this.bar()
            }
        }
    }
}

/* GENERATED_FIR_TAGS: additiveExpression, assignment, classDeclaration, companionObject, funWithExtensionReceiver,
functionDeclaration, functionalType, init, integerLiteral, lambdaLiteral, nestedClass, objectDeclaration,
primaryConstructor, propertyDeclaration, thisExpression, typeWithExtension */
