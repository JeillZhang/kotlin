// RUN_PIPELINE_TILL: FIR2IR
// FIR_IDENTICAL
// FILE: XYZ.java
public interface XYZ<X extends Y, Y extends Z, Z extends Y> {
    XYZ foo() {}
}

// FILE: main.kt

fun main(xyz: XYZ<*, *, *>) = xyz.foo()

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaType, starProjection */
