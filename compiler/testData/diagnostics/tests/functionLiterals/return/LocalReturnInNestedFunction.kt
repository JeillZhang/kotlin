// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

fun test() {
    val x = run f@{
      fun local(a: Int): String {
        if (a > 0) return "2"
        return@local "3"
      }
      return@f 1
    }
    checkSubtype<Int>(x)
}

/* GENERATED_FIR_TAGS: classDeclaration, comparisonExpression, funWithExtensionReceiver, functionDeclaration,
functionalType, ifExpression, infix, integerLiteral, lambdaLiteral, localFunction, localProperty, nullableType,
propertyDeclaration, stringLiteral, typeParameter, typeWithExtension */
