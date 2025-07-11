// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

interface MemoizedFunctionToNotNull<K, V>

fun <K, V : Any> createMemoizedFunction(compute: (K) -> V): MemoizedFunctionToNotNull<K, V> = TODO()

interface A

interface TypeConstructor

class Refiner {
    val memoizedFunctionLambda = <!CANNOT_INFER_PARAMETER_TYPE, CANNOT_INFER_PARAMETER_TYPE!>createMemoizedFunction<!> <!CANNOT_INFER_PARAMETER_TYPE!>{ it.foo() }<!> // error type infered, no diagnostic, BAD, backend fails
    val memoizedFunctionReference = createMemoizedFunction(TypeConstructor::<!EXTENSION_IN_CLASS_REFERENCE_NOT_ALLOWED!>foo<!>) // EXTENSION_IN_CLASS_REFERENCE_IS_NOT_ALLOWED, fine
    val memoizedFunctionTypes = createMemoizedFunction<TypeConstructor, Boolean> { it.foo() } // works fine

    private fun TypeConstructor.foo(): Boolean = true
}

/* GENERATED_FIR_TAGS: callableReference, classDeclaration, funWithExtensionReceiver, functionDeclaration,
functionalType, interfaceDeclaration, lambdaLiteral, nullableType, propertyDeclaration, typeConstraint, typeParameter */
