// RUN_PIPELINE_TILL: BACKEND
class Array<E>(e: E) {
    val k = Array(1) {
        1 as Any
        e as Any?
    }
}
