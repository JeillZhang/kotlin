// LANGUAGE: +ProhibitUsingNullableTypeParameterAgainstNotNullAnnotated +TypeEnhancementImprovementsInStrictMode
// JSPECIFY_STATE: strict
// MUTE_FOR_PSI_CLASS_FILES_READING

// FILE: NonPlatformTypeParameter.java
import org.jspecify.annotations.*;

public class NonPlatformTypeParameter<T extends @Nullable Object> {
    public void foo(T t) {}
    public <E extends @Nullable Object> void bar(E e) {}
}

// FILE: Test.java
public class Test {}

// FILE: main.kt
fun <T : Test> main(a1: NonPlatformTypeParameter<Any?>, a2: NonPlatformTypeParameter<Test>, x: T): Unit {
    a1.foo(null)
    a1.bar<Test?>(null)
    a1.bar<T>(null)
    a1.bar<T>(x)

    a2.foo(null)
    a2.bar<Test?>(null)
    a2.bar<T>(null)
    a2.bar<T>(x)
}

fun testNullable(a1: NonPlatformTypeParameter<Test>, x: Test?) {
    a1.foo(x)
}
