// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_VARIABLE

fun testWhen(x: Any?) {
    val y = when (x) {
        null -> ""
        else -> ::<!UNRESOLVED_REFERENCE!>unresolved<!>
    }
}

fun testWhenWithBraces(x: Any?) {
    val z = when(x) {
        null -> { "" }
        else -> { ::<!UNRESOLVED_REFERENCE!>unresolved<!> }
    }
}

fun testIf(x: Any?) {
    val y = if (x != null) ::<!UNRESOLVED_REFERENCE!>unresolved<!> else null
}

fun testIfWithBraces(x: Any?) {
    val z = if (x != null) { ::<!UNRESOLVED_REFERENCE!>unresolved<!> } else { null }
}

fun testElvis(x: Any?) {
    val y = x ?: ::<!UNRESOLVED_REFERENCE!>unresolved<!>
}

fun testExclExcl() {
    val y = <!NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>:: <!UNRESOLVED_REFERENCE, UNRESOLVED_REFERENCE!>unresolved<!><!><!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>
}

fun testTry() {
    val v = try { ::<!UNRESOLVED_REFERENCE!>unresolved<!> } catch (e: Exception) {}
}

/* GENERATED_FIR_TAGS: checkNotNullCall, elvisExpression, equalityExpression, functionDeclaration, ifExpression,
localProperty, nullableType, propertyDeclaration, stringLiteral, tryExpression, whenExpression, whenWithSubject */
