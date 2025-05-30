/*
 * Copyright 2010-2017 JetBrains s.r.o.
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

package org.jetbrains.kotlin.incremental.components

import org.jetbrains.kotlin.container.DefaultImplementation
import java.io.File

@DefaultImplementation(ExpectActualTracker.DoNothing::class)
interface ExpectActualTracker {
    fun report(expectedFile: File, actualFile: File)
    fun reportExpectOfLenientStub(expectedFile: File)

    object DoNothing : ExpectActualTracker {
        override fun report(expectedFile: File, actualFile: File) {
        }

        override fun reportExpectOfLenientStub(expectedFile: File) {
        }
    }
}