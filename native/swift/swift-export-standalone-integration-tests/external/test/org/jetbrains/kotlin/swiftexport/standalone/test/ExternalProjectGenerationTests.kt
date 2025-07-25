/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.swiftexport.standalone.test

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.TestDataPath
import org.jetbrains.kotlin.konan.test.blackbox.support.TestCase
import org.jetbrains.kotlin.konan.test.blackbox.support.compilation.TestCompilationArtifact
import org.jetbrains.kotlin.konan.test.blackbox.support.group.UseStandardTestCaseGroupProvider
import org.jetbrains.kotlin.konan.test.testLibraryAKlibFile
import org.jetbrains.kotlin.konan.test.testLibraryKotlinxSerializationCore
import org.jetbrains.kotlin.swiftexport.standalone.SwiftExportModule
import org.jetbrains.kotlin.swiftexport.standalone.config.SwiftModuleConfig
import org.jetbrains.kotlin.swiftexport.standalone.runSwiftExport
import org.jetbrains.kotlin.test.TestMetadata
import org.junit.jupiter.api.Test
import java.io.File

@TestMetadata("native/swift/swift-export-standalone-integration-tests/external/testData/generation")
@TestDataPath("\$PROJECT_ROOT")
@UseStandardTestCaseGroupProvider
class ExternalProjectGenerationTests : AbstractSwiftExportWithBinaryCompilationTest(), SwiftExportValidator {

    private val tmpdir = FileUtil.createTempDirectory("SwiftExportIntegrationTests", null, false)

    override fun runCompiledTest(
        testPathFull: File,
        testCase: TestCase,
        swiftExportOutputs: Set<SwiftExportModule>,
        swiftModules: Set<TestCompilationArtifact.Swift.Module>,
        kotlinBinaryLibrary: TestCompilationArtifact.BinaryLibrary,
    ) {
        validateSwiftExportOutput(testPathFull, swiftExportOutputs)
    }

    @Test
    fun `full export of testLibraryA`() {
        val klibSettings = KlibExportSettings(
            testLibraryAKlibFile,
            targets.testTarget,
            "testLibraryA",
        )
        validateFullLibraryDump(klibSettings, "testLibraryA_full_dump")
    }

    @Test
    fun `kotlinx-serialization-core`() {
        val klibSettings = KlibExportSettings(
            testLibraryKotlinxSerializationCore,
            targets.testTarget,
            "KotlinSerialization",
            "kotlinx.serialization",
        )
        validateFullLibraryDump(klibSettings, "kotlinx-serialization-core")
    }

    private fun validateFullLibraryDump(
        klib: KlibExportSettings,
        goldenData: String,
        validateKotlinBridge: Boolean = false,
    ) {
        val config = klib.createConfig(outputPath = tmpdir.toPath().resolve(klib.swiftModuleName))
        val inputModule = klib.createInputModule(
            SwiftModuleConfig(rootPackage = klib.rootPackage, shouldBeFullyExported = true)
        )
        val result = runSwiftExport(setOf(inputModule), config).getOrThrow()
        validateSwiftExportOutput(testDataDir.resolve(goldenData), result, validateKotlinBridge)
    }

}

private val testDataDir = File("native/swift/swift-export-standalone-integration-tests/external/testData/generation")