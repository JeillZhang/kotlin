// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL

val a = 42

class A

enum class E {
    V
}

object O {
    val v = 42
}

fun foo() = 42

val b =  a

val ca = A()

val ev = E.V

val ov = O.v

val rfoo = foo()

/* GENERATED_FIR_TAGS: classDeclaration, enumDeclaration, enumEntry, functionDeclaration, integerLiteral, localProperty,
objectDeclaration, propertyDeclaration */
