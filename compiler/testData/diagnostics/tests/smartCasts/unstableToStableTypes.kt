// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
class Bar {
    fun bar() {}
}

class Foo(var x: Any) {
    init {
        if (x is Bar) {
            val y = x
            // Error: x is not stable, Type(y) = Any
            <!SMARTCAST_IMPOSSIBLE!>x<!>.bar()
            y.<!UNRESOLVED_REFERENCE!>bar<!>()
            if (y == x) {
                // Still error
                y.<!UNRESOLVED_REFERENCE!>bar<!>()
            }
            if (x !is Bar && y != x) {
                // Still error
                y.<!UNRESOLVED_REFERENCE!>bar<!>()
            }
        }
    }
}

/* GENERATED_FIR_TAGS: andExpression, classDeclaration, equalityExpression, functionDeclaration, ifExpression, init,
isExpression, localProperty, primaryConstructor, propertyDeclaration, smartcast */
