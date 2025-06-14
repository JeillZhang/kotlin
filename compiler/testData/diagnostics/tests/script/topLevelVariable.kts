// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER -UNUSED_VARIABLE

import kotlin.reflect.KProperty

class Delegate {
    operator fun getValue(t: Any?, p: KProperty<*>): Int {
        return 1
    }
}

val a: Int by Delegate()

class Foo {
    val a: Int by Delegate()
}

fun foo() {
    val a: Int by Delegate()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, integerLiteral, localProperty, nullableType, operator,
propertyDeclaration, propertyDelegate, starProjection */
