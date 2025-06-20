// RUN_PIPELINE_TILL: BACKEND
interface I<F> {
    fun <T : Comparable<T>> f(t: List<T>, f: List<F>): Any// T = D, List<D> == List<D>
}

abstract class Base<E> {
    fun <D : Comparable<D>> f(t: List<D>, f: List<E>) {}
}


class C : Base<String>(), I<String>

fun f(list: List<Int>, s: List<String>) {
    C().f(list, s)
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, nullableType, typeConstraint,
typeParameter */
