// DISABLE_JAVA_FACADE
// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// JAVAC_EXPECTED_FILE
// FILE: p/G.java

package p;

public interface G<TG> {
}

// FILE: p/A.kt

package p;

public interface A<TA> {
    fun foo(p: A<TA>)
}

// FILE: p/B.java

package p;

public class B<TB> implements A<TB> {
    void foo(A<TB> p) {}
}

// FILE: p/C.java

package p;

public class C<TC> extends B<TC> implements A<TC> {
}

// FILE: p/P.java

package p;

public class P {
}

// FILE: k.kt

import p.*

abstract class K: C<P>() {

}

abstract class AL: java.util.ArrayList<P>() {

}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, javaType, nullableType,
typeParameter */
