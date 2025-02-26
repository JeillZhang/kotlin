/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt.test

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.codegen.ClassBuilderMode
import org.jetbrains.kotlin.codegen.GenerationUtils
import org.jetbrains.kotlin.codegen.OriginCollectingClassBuilderFactory
import org.jetbrains.kotlin.fir.backend.Fir2IrComponentsStorage
import org.jetbrains.kotlin.fir.backend.jvm.FirJvmBackendClassResolver
import org.jetbrains.kotlin.kapt.KaptContextForStubGeneration
import org.jetbrains.kotlin.kapt.util.MessageCollectorBackedKaptLogger
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.services.*

class JvmCompilerWithKaptFacade(
    private val testServices: TestServices,
    private val additionalPluginExtension: IrGenerationExtension? = null,
) :
    AbstractTestFacade<ResultingArtifact.Source, KaptContextBinaryArtifact>() {
    override val inputKind: TestArtifactKind<ResultingArtifact.Source>
        get() = SourcesKind
    override val outputKind: TestArtifactKind<KaptContextBinaryArtifact>
        get() = KaptContextBinaryArtifact.Kind

    override val additionalServices: List<ServiceRegistrationData>
        get() = listOf(service(::KaptMessageCollectorProvider))

    override fun transform(module: TestModule, inputArtifact: ResultingArtifact.Source): KaptContextBinaryArtifact {
        val configurationProvider = testServices.compilerConfigurationProvider
        val project = configurationProvider.getProject(module)
        if (additionalPluginExtension != null) {
            IrGenerationExtension.registerExtension(project, additionalPluginExtension)
        }
        val ktFiles = testServices.sourceFileProvider.getKtFilesForSourceFiles(module.files, project, findViaVfs = true).values.toList()
        val classBuilderFactory = OriginCollectingClassBuilderFactory(ClassBuilderMode.KAPT3)
        val (generationState, bindingContext) = GenerationUtils.compileFiles(
            ktFiles,
            configurationProvider.getCompilerConfiguration(module),
            classBuilderFactory,
            configurationProvider.getPackagePartProviderFactory(module)
        )
        val logger = MessageCollectorBackedKaptLogger(
            isVerbose = true,
            isInfoAsWarnings = false,
            messageCollector = testServices.messageCollectorProvider.getCollector(module)
        )
        val firFiles =
            ((generationState.jvmBackendClassResolver as? FirJvmBackendClassResolver)?.components as? Fir2IrComponentsStorage)?.fir.orEmpty()
        val kaptContext = KaptContextForStubGeneration(
            testServices.kaptOptionsProvider[module],
            withJdk = true,
            logger,
            classBuilderFactory.compiledClasses,
            classBuilderFactory.origins,
            generationState,
            bindingContext,
            firFiles,
        )
        return KaptContextBinaryArtifact(kaptContext)
    }

    override fun shouldTransform(module: TestModule): Boolean {
        return true // TODO
    }
}

class KaptContextBinaryArtifact(val kaptContext: KaptContextForStubGeneration) : ResultingArtifact.Binary<KaptContextBinaryArtifact>() {
    object Kind : ArtifactKind<KaptContextBinaryArtifact>("KaptArtifact")

    override val kind: ArtifactKind<KaptContextBinaryArtifact>
        get() = Kind
}
