// FIR_IDENTICAL
// JSPECIFY_STATE: warn
// DIAGNOSTICS: -UNUSED_PARAMETER

// FILE: NullMarkedType.java

import org.jspecify.annotations.*;

@NullMarked
public class NullMarkedType {

    public static class TargetType<T extends @Nullable Object> {

        public @Nullable T produce() { return null; }

        @NullUnmarked
        public static TargetType INSTANCE() { return new TargetType<String>(); }

    }

}

// FILE: kotlin.kt

fun <T> accept(arg: T) {}

fun test() {
    accept<Any>(<!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>NullMarkedType.TargetType.INSTANCE().produce()<!>)
}
