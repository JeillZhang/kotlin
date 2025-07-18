// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-66350

annotation class TestParameters(val param: String)

object TestVeConsts {
    const val LETS_GO_BUTTON = 1
}

val ConstsDuplicate = TestVeConsts

@TestParameters(
    "{ veId: ${ConstsDuplicate.LETS_GO_BUTTON}}"
)
fun box() = "OK"

// Additional examples

annotation class WithDefaultValue(val value: Int = ConstsDuplicate.LETS_GO_BUTTON + 1)

/* GENERATED_FIR_TAGS: additiveExpression, annotationDeclaration, const, functionDeclaration, integerLiteral,
objectDeclaration, primaryConstructor, propertyDeclaration, stringLiteral */
