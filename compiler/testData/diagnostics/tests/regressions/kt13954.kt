// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: foo/A.kt

package foo

class A(val c: C)

// FILE: foo/B.kt

package foo

class B {
    interface D {
        fun foo(): E
    }

    class E
}

// FILE: foo/C.java

package foo;

import static foo.B.D.*;

@SuppressWarnings("RedundantTypeArguments")
public class C {}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, javaType, nestedClass,
primaryConstructor, propertyDeclaration */
