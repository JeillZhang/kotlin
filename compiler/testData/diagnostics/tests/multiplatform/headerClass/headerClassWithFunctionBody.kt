// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: FIR2IR
// MODULE: m1-common
// FILE: common.kt
expect class <!NO_ACTUAL_FOR_EXPECT{JVM}!>Foo<!> {
    <!EXPECTED_DECLARATION_WITH_BODY, EXPECTED_DECLARATION_WITH_BODY{JVM}!>init<!> {
        "no"
    }

    <!EXPECTED_DECLARATION_WITH_BODY, EXPECTED_DECLARATION_WITH_BODY{JVM}!>constructor(s: String)<!> {
        "no"
    }

    constructor() : <!EXPECTED_CLASS_CONSTRUCTOR_DELEGATION_CALL, EXPECTED_CLASS_CONSTRUCTOR_DELEGATION_CALL{JVM}!>this<!>("no")

    val prop: String = <!EXPECTED_PROPERTY_INITIALIZER, EXPECTED_PROPERTY_INITIALIZER{JVM}!>"no"<!>

    var getSet: String
        <!EXPECTED_DECLARATION_WITH_BODY, EXPECTED_DECLARATION_WITH_BODY{JVM}!>get()<!> = "no"
        <!EXPECTED_DECLARATION_WITH_BODY, EXPECTED_DECLARATION_WITH_BODY{JVM}!>set(value)<!> {}

    <!EXPECTED_DECLARATION_WITH_BODY, EXPECTED_DECLARATION_WITH_BODY{JVM}!>fun functionWithBody(x: Int): Int<!> {
        return x + 1
    }
}

// MODULE: m1-jvm()()(m1-common)

/* GENERATED_FIR_TAGS: additiveExpression, classDeclaration, expect, functionDeclaration, getter, init, integerLiteral,
propertyDeclaration, secondaryConstructor, setter, stringLiteral */
