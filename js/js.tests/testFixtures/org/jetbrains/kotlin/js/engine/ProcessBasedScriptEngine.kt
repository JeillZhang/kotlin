/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.engine

import com.intellij.openapi.util.text.StringUtil
import java.util.concurrent.atomic.AtomicInteger

private val LINE_SEPARATOR = System.getProperty("line.separator")!!
private val END_MARKER = "<END>$LINE_SEPARATOR"
private val ESM_EXTENSION = ".mjs"

private val counter = AtomicInteger(0)

abstract class ProcessBasedScriptEngine(
    private val executablePath: String,
    private val doTrace: Boolean
) : ScriptEngine {

    private var process: Process? = null
    private val buffer = ByteArray(1024)

    override fun eval(script: String): String {
        val vm = getOrCreateProcess()

        val stdin = vm.outputStream
        val stdout = vm.inputStream
        val stderr = vm.errorStream

        val writer = stdin.writer()
        writer.write(StringUtil.convertLineSeparators(script, "\\n") + "\n")
        writer.flush()

        val out = StringBuilder()

        while (vm.isAlive) {
            val n = stdout.available()
            if (n == 0) continue

            val count = stdout.read(buffer)

            val s = String(buffer, 0, count)
            out.append(s)

            if (out.endsWith(END_MARKER)) break
        }

        if (stderr.available() > 0) {
            val err = StringBuilder()

            while (vm.isAlive && stderr.available() > 0) {
                val count = stderr.read(buffer)
                val s = String(buffer, 0, count)
                err.append(s)
            }

            throw ScriptExecutionException(out.toString(), err.toString())
        }

        return out.removeSuffix(END_MARKER).removeSuffix(LINE_SEPARATOR).toString()
    }

    override fun loadFile(path: String) {
        if (path.endsWith(ESM_EXTENSION)) return
        eval("load('${path.replace('\\', '/')}');")
    }

    override fun reset() {
        eval("!reset")
    }

    override fun saveGlobalState() {
        eval("!saveGlobalState")
    }

    override fun restoreGlobalState() {
        eval("!restoreGlobalState")
    }

    override fun release() {
        process?.destroy()
        process = null
        if (doTrace)
            println("Release repl.js #${counter.decrementAndGet()} in thread ${Thread.currentThread().id}")
    }

    private fun getOrCreateProcess(): Process {
        val p = process

        if (p != null && p.isAlive) return p

        process = null

        if (doTrace)
            println("Started repl.js #${counter.getAndIncrement()} in thread ${Thread.currentThread().id}")
        val builder = ProcessBuilder(
            executablePath,
            "js/js.tests/testFixtures/org/jetbrains/kotlin/js/engine/repl.js",
        )
        return builder.start().also {
            process = it
        }
    }
}

class ScriptExecutionException(val stdout: String, val stderr: String) : RuntimeException() {
    override val message: String
        get() = "ERROR:\n$stderr\nOUTPUT:\n$stdout"
}
