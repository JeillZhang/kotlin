// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
fun <T1> List<T1>?.foo() {}

@JvmName("foo1")
fun <T2> List<T2>.foo() {}


fun <T1> bar(x: List<T1>) = x

@JvmName("bar1")
fun <T2> bar(x: List<T2>?) = x

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, nullableType, stringLiteral, typeParameter */
