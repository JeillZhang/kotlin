// SKIP_INLINE_CHECK_IN: inlineFun$default
// FILE: 1.kt
package test

class A(val value: String) {

    inline fun String.inlineFun(crossinline lambda: () -> String, crossinline dlambda: () -> String = { this }): String {
        return {
            "$value ${this} ${lambda()} ${dlambda()}"
        }.let { it() }
    }
}

// FILE: 2.kt
//WIH_RUNTIME
import test.*

fun String.test(): String = with(A("VALUE")) { "INLINE".inlineFun({ this@test }) }

fun box(): String {
    val result = "TEST".test()
    return if (result == "VALUE INLINE TEST INLINE") "OK" else "fail 1: $result"
}
