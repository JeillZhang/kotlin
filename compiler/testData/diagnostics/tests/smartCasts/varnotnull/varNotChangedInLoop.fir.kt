// RUN_PIPELINE_TILL: BACKEND
public fun foo() {
    var i: Int? = 1
    if (i != null) {
        while (i != 10) {
            i++
        }
    }
}

/* GENERATED_FIR_TAGS: assignment, equalityExpression, functionDeclaration, ifExpression, incrementDecrementExpression,
integerLiteral, localProperty, nullableType, propertyDeclaration, smartcast, whileLoop */
