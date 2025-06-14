// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: KT-66336

interface A : Comparable<A>
interface B : Comparable<B>

open class Base<T>
class Derived<T : Comparable<T>, in S : T?>(expr: Base<in S>) : Base<T?>()

fun call(f: (String) -> Base<*>? = { null }) {}

fun test(b: Base<B>) {
    call {
        when (it) {
            "a" -> b
            "b" -> Derived(Base<A>())
            else -> null
        }
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, equalityExpression, functionDeclaration, functionalType, in, inProjection,
interfaceDeclaration, lambdaLiteral, nullableType, primaryConstructor, starProjection, stringLiteral, typeConstraint,
typeParameter, whenExpression, whenWithSubject */
