// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
open class A : <!CYCLIC_INHERITANCE_HIERARCHY!>B<!>()
open class B : <!CYCLIC_INHERITANCE_HIERARCHY!>A<!>()

fun <T> select(vararg xs: T): T = xs[0]

fun foo() {
    val x = select(A(), B(), "foo")
}

/* GENERATED_FIR_TAGS: capturedType, classDeclaration, functionDeclaration, integerLiteral, localProperty, nullableType,
outProjection, propertyDeclaration, stringLiteral, typeParameter, vararg */
