// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER -UNUSED_VARIABLE

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.jvm.JvmName("containsAny")
@kotlin.internal.LowPriorityInOverloadResolution
public fun <T> Iterable<T>.contains1(element: T): Int = null!!

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
public fun <T> Iterable<T>.contains1(element: @kotlin.internal.NoInfer T): Boolean = null!!


fun test() {
    val a: Boolean = listOf(1).contains1(<!TYPE_MISMATCH!>""<!>)
    val b: Boolean = listOf(1).contains1(1)
}

/* GENERATED_FIR_TAGS: checkNotNullCall, funWithExtensionReceiver, functionDeclaration, integerLiteral, localProperty,
nullableType, propertyDeclaration, stringLiteral, typeParameter */
