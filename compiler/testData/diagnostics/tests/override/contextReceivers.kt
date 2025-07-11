// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -CONTEXT_RECEIVERS_DEPRECATED
// LANGUAGE: +ContextReceivers

interface I {
    context(String, Int) fun foo()
}

class C1 : I {
    context(String, Int) override fun foo() {}
}

class C2 : I {
    context(String) override fun foo() {}
}

class C3 : I {
    context(Int, String) override fun foo() {}
}

class C4 : I {
    context(String, Float) override fun foo() {}
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, functionDeclarationWithContext, interfaceDeclaration,
override */
