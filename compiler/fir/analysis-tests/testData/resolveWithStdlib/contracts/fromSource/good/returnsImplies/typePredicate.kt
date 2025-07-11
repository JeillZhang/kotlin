// RUN_PIPELINE_TILL: FRONTEND
import kotlin.contracts.*

@OptIn(ExperimentalContracts::class)
fun checkIsString(x: Any): Boolean {
    contract {
        returns(true) implies (x is String)
        returns(false) implies (x !is String)
    }
    return x is String
}

fun test(x: Any) {
    if (checkIsString(x)) {
        x.length // OK
    } else {
        x.<!UNRESOLVED_REFERENCE!>length<!> // Error
    }
}

/* GENERATED_FIR_TAGS: classReference, contractConditionalEffect, contracts, functionDeclaration, ifExpression,
isExpression, lambdaLiteral, smartcast */
