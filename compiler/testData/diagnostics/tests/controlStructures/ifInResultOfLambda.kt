// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// NI_EXPECTED_FILE

val test1 = { if (true) 1 else "" }

val test2 = { { if (true) 1 else "" } }

val test3: (Boolean) -> Any = { if (it) 1 else "" }

val test4: (Boolean) -> Any? = { if (it) 1 else "" }

/* GENERATED_FIR_TAGS: functionalType, ifExpression, integerLiteral, intersectionType, lambdaLiteral, nullableType,
propertyDeclaration, stringLiteral */
