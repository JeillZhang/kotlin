// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
package a

class A {}

fun test(a1: A, a2: A) {
    val range = "island".."isle"

    a1<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>..<!>a2
}


public operator fun <T: Comparable<T>> T.rangeTo(that: T): ClosedRange<T> {
    throw UnsupportedOperationException()
}

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, localProperty, operator,
propertyDeclaration, rangeExpression, stringLiteral, typeConstraint, typeParameter */
