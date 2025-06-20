// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
public interface Base<T> {
    fun test(p: T) = "Base"
}

public interface Derived<Y> : Base<Y>

class Delegate<Z> : Derived<Z>

fun box(): String {
    object :  Derived<String> by Delegate() {
    }
    return "OK"
}

/* GENERATED_FIR_TAGS: anonymousObjectExpression, classDeclaration, functionDeclaration, inheritanceDelegation,
interfaceDeclaration, nullableType, stringLiteral, typeParameter */
