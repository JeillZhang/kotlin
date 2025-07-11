// RUN_PIPELINE_TILL: BACKEND
// LANGUAGE: +EnumEntries +PrioritizedEnumEntries
// WITH_STDLIB
// FIR_DUMP

package foo

import foo.A.Companion.entries

enum class A {
    ;

    companion object {
        val entries = 0
    }
}

fun test() {
    A.<!DEPRECATED_ACCESS_TO_ENUM_ENTRY_COMPANION_PROPERTY!>entries<!>

    with(A) {
        entries
    }
}

/* GENERATED_FIR_TAGS: companionObject, enumDeclaration, functionDeclaration, integerLiteral, lambdaLiteral,
objectDeclaration, propertyDeclaration */
