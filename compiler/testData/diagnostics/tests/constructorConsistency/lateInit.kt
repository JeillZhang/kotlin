// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
class WithLateInit {
    lateinit var x: String

    fun init(xx: String) {
        x = xx
    }

    init {
        // This is obviously a bug,
        // but with lateinit we explicitly want it to occur in runtime
        use()
    }

    fun use() = x
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, functionDeclaration, init, lateinit, propertyDeclaration */
