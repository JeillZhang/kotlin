// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: p/p.kt
package p

object Object {
    sealed class Sealed1 {
        sealed class Sealed2 {
        }
    }
}

// FILE: a/x.java
package a;

import p.Object;

public class x {
    public Object.Sealed1.Sealed2 getSealed2() { return null; };
}

// FILE: test.kt
package a

fun test() = x().getSealed2()

/* GENERATED_FIR_TAGS: classDeclaration, flexibleType, functionDeclaration, javaFunction, javaType, nestedClass,
objectDeclaration, sealed */
