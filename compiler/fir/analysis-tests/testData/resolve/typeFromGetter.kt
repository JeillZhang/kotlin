// RUN_PIPELINE_TILL: BACKEND
val x get() = 1

val y: Int get() = 1

val z = 1
    get() = field

val w: Int get(): Int = 1

interface Some {
    val bar: Int get() = 1
}

/* GENERATED_FIR_TAGS: getter, integerLiteral, interfaceDeclaration, propertyDeclaration */
