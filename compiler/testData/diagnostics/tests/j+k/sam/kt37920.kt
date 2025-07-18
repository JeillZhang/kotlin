// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_VARIABLE

// FILE: A.java

public class A {
    public static <T, E extends Throwable> T compute(ThrowableComputable<T, E> action) throws E {
        return (T) action;
    }
}

// FILE: ThrowableComputable.java

public interface ThrowableComputable<T, E extends Throwable> {
    T compute() throws E;
}

// FILE: test.kt

fun main() {
    val headers = A.compute(
        ThrowableComputable {
            getCollection()
        }
    )
}

fun getCollection(): Collection<Int> = TODO()

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaFunction, javaType, lambdaLiteral, localProperty,
propertyDeclaration */
