// RUN_PIPELINE_TILL: FRONTEND
// CHECK_TYPE
var x
    get() = 1
    set(q) {
        q checkType { _<Int>() }
    }

<!MUST_BE_INITIALIZED!>var noSetter<!>
    get() = 1


fun foo() {
    x checkType { _<Int>() }
    noSetter checkType { _<Int>() }

    x = 1
    x = <!TYPE_MISMATCH!>""<!>

    noSetter = 2
    noSetter = <!TYPE_MISMATCH!>""<!>
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, funWithExtensionReceiver, functionDeclaration, functionalType,
getter, infix, integerLiteral, lambdaLiteral, nullableType, propertyDeclaration, setter, stringLiteral, typeParameter,
typeWithExtension */
