// ES_MODULES
// SKIP_NODE_JS

// MODULE: lib
// FILE: lib.kt
@file:JsExport

@JsName("foo")
public fun foo(k: String): String = "O$k"

// FILE: enty.mjs
// ENTRY_ES_MODULE

import { foo } from "./nonIndetifierModuleNameInExportedFile-lib_v5.mjs";

export function box() {
    return foo("K")
}
