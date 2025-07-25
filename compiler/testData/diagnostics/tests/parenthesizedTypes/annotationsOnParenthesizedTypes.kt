// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER

interface AnnotationsOnParenthesizedTypes {
    fun B<(@A C)>.receiverArgument() {}

    fun parameter(a: (@A C)) {}

    fun parameterArgument(a: B<(@A C)>) {}

    fun returnValue(): (@A C)

    fun <T> returnTypeParameterValue(): (@A T)

    fun returnArgument(): B<(@A C)>

    val lambdaType: (@A() (() -> C))

    val lambdaParameter: ((@A C)) -> C

    val lambdaReturnValue: () -> (@A C)

    val lambdaReceiver: (@A C).() -> C

    val lambdaParameterNP: (@A C) -> C
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER)
annotation class A

interface B<T>
interface C

/* GENERATED_FIR_TAGS: annotationDeclaration, funWithExtensionReceiver, functionDeclaration, functionalType,
interfaceDeclaration, nullableType, propertyDeclaration, typeParameter, typeWithExtension */
