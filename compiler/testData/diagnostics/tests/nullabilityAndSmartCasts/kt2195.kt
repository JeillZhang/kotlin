// RUN_PIPELINE_TILL: BACKEND
//KT-2195 error "Only safe calls are allowed ..." but it is function param (val)
package foo

private fun <T> sendCommand(errorCallback: (()->Unit)? = null) {
    if (errorCallback != null) {
        <!DEBUG_INFO_SMARTCAST!>errorCallback<!>()
    }
}

/* GENERATED_FIR_TAGS: equalityExpression, functionDeclaration, functionalType, ifExpression, nullableType, smartcast,
typeParameter */
