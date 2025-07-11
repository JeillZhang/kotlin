// RUN_PIPELINE_TILL: FIR2IR
// DIAGNOSTICS: -UNUSED_PARAMETER

// FILE: A.java

public class A {
    public static <T> T flexible(T x) { return x; }
}


// FILE: test.kt

class Inv<K>

fun launch(block: () -> Unit) {}
fun <T> run(block: () -> T): T = block()
fun <T> run(inv: Inv<T>, block: () -> T): T = block()

fun test(i: Inv<Nothing>, iUnit: Inv<Unit>) {
    launch {
        run<Nothing> { TODO("") }
    }
    launch {
        run { TODO("") }
    }
    launch {
        run<String> { "" }
    }
    launch {
        run<Nothing?> { null }
    }
    launch {
        run { null }
    }
    launch {
        run(i) { TODO() }
    }
    launch {
        run(A.flexible(i)) { TODO() }
    }
    launch {
        run(A.flexible(iUnit)) { 42 }
    }
    launch {
        @Suppress(<!ERROR_SUPPRESSION!>"UNSUPPORTED"<!>)
        run<dynamic> { "" }
    }

    if (<!USELESS_IS_CHECK!>iUnit is String<!>) {
        launch {
            run(A.<!INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING!>flexible<!>(iUnit)) { 42 }
        }
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, flexibleType, functionDeclaration, functionalType, ifExpression, integerLiteral,
intersectionType, isExpression, javaFunction, lambdaLiteral, nullableType, smartcast, stringLiteral, typeParameter */
