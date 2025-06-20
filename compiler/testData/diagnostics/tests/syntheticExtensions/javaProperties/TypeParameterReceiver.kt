// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: KotlinFile.kt

fun <T: B> foo(t: T) {
    t.setSomething(t.getSomething() + 1)
    t.something++
}

// FILE: A.java
public interface A {
    int getSomething();
}

// FILE: B.java
public interface B extends A {
    void setSomething(int value);
}

/* GENERATED_FIR_TAGS: additiveExpression, assignment, functionDeclaration, incrementDecrementExpression, integerLiteral,
javaFunction, javaType, typeConstraint, typeParameter */
