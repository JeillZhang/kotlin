// RUN_PIPELINE_TILL: BACKEND
package test

abstract class Some {
    companion object {
        class InCompanion
    }

    abstract val x: InCompanion
}

abstract class Another {
    companion object NamedCompanion {
        class InCompanion
    }

    abstract val x: InCompanion
}

/* GENERATED_FIR_TAGS: classDeclaration, companionObject, nestedClass, objectDeclaration, propertyDeclaration */
