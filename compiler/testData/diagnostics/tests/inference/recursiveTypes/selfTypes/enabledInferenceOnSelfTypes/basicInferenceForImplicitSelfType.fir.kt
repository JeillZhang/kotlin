// RUN_PIPELINE_TILL: BACKEND

// FILE: JavaBuilder.java
public class JavaBuilder<B extends JavaBuilder<B>> {
    <T extends B> T test() {
        return null;
    }
    void foo() { }
}

// FILE: main.kt
class Builder<B : Builder<B>> {
    fun <T : B> test(): T = TODO()

    fun foo() {}
    fun bar(block: () -> Out<B>) {}
}

class Out<out T>

fun testStar(builder: Builder<*>) {
    <!DEBUG_INFO_EXPRESSION_TYPE("CapturedType(*)")!>builder.test()<!>

    builder
        .test()
        .foo()

    builder
        .test()
        .bar { Out() }
}

fun <K : Builder<K>> testTypeParam(builder: Builder<K>) {
    <!DEBUG_INFO_EXPRESSION_TYPE("K")!>builder.test()<!>

    builder
        .test()
        .foo()

    builder
        .test()
        .bar { Out() }
}

fun testStarJava(builder: JavaBuilder<*>) {
    <!DEBUG_INFO_EXPRESSION_TYPE("(CapturedType(*)..CapturedType(*)?)")!>builder.test()<!>

    builder
        .test()
        .foo()
}

fun <K : JavaBuilder<K>> testTypeParamJava(builder: JavaBuilder<K>) {
    <!DEBUG_INFO_EXPRESSION_TYPE("(K..K?)")!>builder.test()<!>

    builder
        .test()
        .foo()
}

/* GENERATED_FIR_TAGS: capturedType, classDeclaration, flexibleType, functionDeclaration, functionalType, javaType,
lambdaLiteral, nullableType, out, starProjection, typeConstraint, typeParameter */
