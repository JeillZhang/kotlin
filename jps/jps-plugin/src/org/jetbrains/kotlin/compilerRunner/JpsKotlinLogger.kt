/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.compilerRunner

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.kotlin.buildtools.api.KotlinLogger

internal class JpsKotlinLogger(private val log: Logger) : KotlinLogger {
    override fun error(msg: String, throwable: Throwable?) {
        log.error(msg, throwable)
    }

    override fun warn(msg: String, throwable: Throwable?) {
        log.warn(msg, throwable)
    }

    override fun info(msg: String) {
        log.info(msg)
    }

    override fun debug(msg: String) {
        log.debug(msg)
    }

    override fun lifecycle(msg: String) {
        log.info(msg)
    }

    override val isDebugEnabled: Boolean
        get() = log.isDebugEnabled
}