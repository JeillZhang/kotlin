// TARGET_BACKEND: JVM_IR
// DUMP_IR
// WITH_STDLIB
// WITH_REFLECT
// FULL_JDK
// LANGUAGE: +ContextParameters

// MODULE: a
import org.jetbrains.kotlin.plugin.sandbox.AllPropertiesConstructor

class A(val s: String)
class B(val s: String)
class C(val s: String)

@AllPropertiesConstructor
open class Base {
    val a: A = A("a")
    val b = B("b")
}

// MODULE: b(a)
// FILE: Derived.kt
import org.jetbrains.kotlin.plugin.sandbox.AllPropertiesConstructor

@AllPropertiesConstructor
class Derived : Base() {
    val c = C("c")
}

// FILE: main.kt
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaConstructor

fun box(): String {
    val constructor = Derived::class.constructors.first { it.valueParameters.size == 3 }.javaConstructor!!
    val derived = constructor.newInstance(A("a"), B("b"), C("c"))
    with(derived) {
        with("") {
            hasExtension()
            hasContext()
        }
    }
    return if (derived != null) "OK" else "Error"
}
