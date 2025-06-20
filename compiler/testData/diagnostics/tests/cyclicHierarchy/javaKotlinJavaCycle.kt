// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// FILE: A.java

interface A extends C {
    void foo();
}

// FILE: B.kt

interface B : <!CYCLIC_INHERITANCE_HIERARCHY!>A<!> {
    fun bar()
}

// FILE: C.java

interface C extends B {
    void baz();
}

/* GENERATED_FIR_TAGS: functionDeclaration, interfaceDeclaration */
