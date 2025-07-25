/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js

import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.InternalKotlinTarget

@Deprecated(
    "The Kotlin/JS legacy target is deprecated and its support completely discontinued. Scheduled for removal in Kotlin 2.3.",
    level = DeprecationLevel.ERROR,
)
abstract class KotlinJsTarget : KotlinTarget, InternalKotlinTarget {
    @Deprecated("Only for compatibility")
    val irTarget = null
}
