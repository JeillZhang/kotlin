// RUN_PIPELINE_TILL: FRONTEND
// FILE: p/My.java

package p;

import org.jetbrains.annotations.*;

class My {
    @Nullable static String create() {
        return "";
    }
}

// FILE: test.kt

package p

fun bar(x: String) = x

fun test(x: String?): Any {
    val y = My.create()
    val z = x ?: y!!
    bar(<!ARGUMENT_TYPE_MISMATCH!>y<!>)
    // !! / ?. is necessary here, because y!! above may not be executed
    y?.hashCode()
    y!!.hashCode()
    return z
}

/* GENERATED_FIR_TAGS: checkNotNullCall, elvisExpression, functionDeclaration, javaFunction, localProperty, nullableType,
propertyDeclaration, safeCall */
