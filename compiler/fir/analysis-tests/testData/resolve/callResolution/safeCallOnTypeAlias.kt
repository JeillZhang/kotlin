// RUN_PIPELINE_TILL: BACKEND
typealias MyTypeAlias = (() -> String?)?
fun foo(x: MyTypeAlias) {

    x?.let { y -> y()?.let { result -> bar(result) } }
}

fun bar(x: String) = x

/* GENERATED_FIR_TAGS: functionDeclaration, functionalType, lambdaLiteral, nullableType, safeCall, typeAliasDeclaration */
