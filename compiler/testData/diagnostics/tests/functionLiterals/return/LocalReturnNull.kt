// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
class A
val flag = true

val a /*: () -> A?*/ = l@ {
    if (flag) return@l null

    A()
}

val b /*: () -> A?*/ = l@ {
    if (flag) return@l null

    return@l A()
}

/* GENERATED_FIR_TAGS: classDeclaration, ifExpression, lambdaLiteral, nullableType, propertyDeclaration */
