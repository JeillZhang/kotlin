// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: KT-57190
// FILE: MyFunction.java
@FunctionalInterface
public interface MyFunction<T> {
    void apply(T t);
}

// FILE: MyPromise.java
public interface MyPromise<T> {
    void then(MyFunction<T> done);
}

// FILE: main.kt
typealias JsExpressionResult = String?

fun countElementsByXpathAsync(promise: MyPromise<JsExpressionResult>, t: JsExpressionResult) {
    promise.then {
        it?.foo() // K1: ok, K2: was UNSAFE_CALL
    }
}

fun String.foo() {}

/* GENERATED_FIR_TAGS: flexibleType, funWithExtensionReceiver, functionDeclaration, javaType, lambdaLiteral,
nullableType, safeCall, samConversion, typeAliasDeclaration */
