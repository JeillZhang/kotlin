/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.incremental

import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.load.kotlin.JvmBytecodeBinaryVersion
import org.jetbrains.kotlin.metadata.deserialization.MetadataVersion
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Manages files with actual version [loadActual] and provides expected version [expected].
 * Based on that actual and expected versions [CacheStatus] can be calculated.
 * This can be done by constructing [CacheAttributesDiff] and calling [CacheAttributesDiff.status].
 * Based on that status system may perform required actions (i.e. rebuild something, clearing caches, etc...).
 */
class CacheVersionManager(
    private val versionFile: Path,
    expectedOwnVersion: Int?
) : CacheAttributesManager<CacheVersion> {
    override val expected: CacheVersion? =
        if (expectedOwnVersion == null) null
        else CacheVersion(expectedOwnVersion, JvmBytecodeBinaryVersion.INSTANCE, MetadataVersion.INSTANCE)

    override fun loadActual(): CacheVersion? =
        if (!versionFile.toFile().exists()) null
        else try {
            CacheVersion(Files.newInputStream(versionFile).bufferedReader().use { it.readText() }.toInt())
        } catch (e: NumberFormatException) {
            null
        } catch (e: IOException) {
            null
        }

    override fun writeVersion(values: CacheVersion?) {
        if (values == null) Files.deleteIfExists(versionFile)
        else {
            Files.createDirectories(versionFile.parent)
            Files.newOutputStream(versionFile).bufferedWriter().use { it.append(values.intValue.toString()) }
        }
    }

    @get:TestOnly
    val versionFileForTesting: File
        get() = versionFile.toFile()
}

fun CacheVersion(own: Int, bytecode: JvmBytecodeBinaryVersion, metadata: MetadataVersion): CacheVersion {
    require(own in 0 until Int.MAX_VALUE / 1000000)
    require(bytecode.major in 0..9)
    require(bytecode.minor in 0..9)
    require(metadata.major in 0..9)
    require(metadata.minor in 0..99)

    return CacheVersion(
        own * 1000000 +
                bytecode.major * 10000 + bytecode.minor * 100 +
                metadata.major * 1000 + metadata.minor
    )
}

data class CacheVersion(val intValue: Int) {
    val own: Int
        get() = intValue / 1000000

    val bytecode: JvmBytecodeBinaryVersion
        get() = JvmBytecodeBinaryVersion(
            intValue / 10000 % 10,
            intValue / 100 % 10
        )

    val metadata: MetadataVersion
        get() = MetadataVersion(
            intValue / 1000 % 10,
            intValue / 1 % 100
        )

    override fun toString(): String = "CacheVersion(caches: $own, bytecode: $bytecode, metadata: $metadata)"
}
