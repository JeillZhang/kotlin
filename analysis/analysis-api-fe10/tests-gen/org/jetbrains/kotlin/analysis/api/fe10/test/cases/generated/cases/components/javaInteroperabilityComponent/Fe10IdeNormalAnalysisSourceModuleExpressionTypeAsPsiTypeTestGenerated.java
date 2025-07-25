/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fe10.test.cases.generated.cases.components.javaInteroperabilityComponent;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.analysis.api.fe10.test.configurator.AnalysisApiFe10TestConfiguratorFactory;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfiguratorFactoryData;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.TestModuleKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.FrontendKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisSessionMode;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiMode;
import org.jetbrains.kotlin.analysis.api.impl.base.test.cases.components.javaInteroperabilityComponent.AbstractExpressionTypeAsPsiTypeTest;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.analysis.api.GenerateAnalysisApiTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression")
@TestDataPath("$PROJECT_ROOT")
public class Fe10IdeNormalAnalysisSourceModuleExpressionTypeAsPsiTypeTestGenerated extends AbstractExpressionTypeAsPsiTypeTest {
  @NotNull
  @Override
  public AnalysisApiTestConfigurator getConfigurator() {
    return AnalysisApiFe10TestConfiguratorFactory.INSTANCE.createConfigurator(
      new AnalysisApiTestConfiguratorFactoryData(
        FrontendKind.Fe10,
        TestModuleKind.Source,
        AnalysisSessionMode.Normal,
        AnalysisApiMode.Ide
      )
    );
  }

  @Test
  public void testAllFilesPresentInForExpression() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression"), Pattern.compile("^(.+)\\.kt$"), null, true);
  }

  @Test
  @TestMetadata("capturedBoundType.kt")
  public void testCapturedBoundType() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/capturedBoundType.kt");
  }

  @Test
  @TestMetadata("class_object_call.kt")
  public void testClass_object_call() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/class_object_call.kt");
  }

  @Test
  @TestMetadata("class_object_constructor.kt")
  public void testClass_object_constructor() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/class_object_constructor.kt");
  }

  @Test
  @TestMetadata("errorType.kt")
  public void testErrorType() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/errorType.kt");
  }

  @Test
  @TestMetadata("flexibleTypeWithArgumentUpperBound.kt")
  public void testFlexibleTypeWithArgumentUpperBound() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/flexibleTypeWithArgumentUpperBound.kt");
  }

  @Test
  @TestMetadata("inlineClassWithArguments.kt")
  public void testInlineClassWithArguments() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/inlineClassWithArguments.kt");
  }

  @Test
  @TestMetadata("inlineClassWithoutArguments.kt")
  public void testInlineClassWithoutArguments() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/inlineClassWithoutArguments.kt");
  }

  @Test
  @TestMetadata("KTIJ25461.kt")
  public void testKTIJ25461() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/KTIJ25461.kt");
  }

  @Test
  @TestMetadata("localClassWithUnresolvedSuperType.kt")
  public void testLocalClassWithUnresolvedSuperType() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/localClassWithUnresolvedSuperType.kt");
  }

  @Test
  @TestMetadata("recursiveTypeParameter_localSimple.kt")
  public void testRecursiveTypeParameter_localSimple() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/recursiveTypeParameter_localSimple.kt");
  }

  @Test
  @TestMetadata("recursiveTypeParameter_localWithTypeParameter.kt")
  public void testRecursiveTypeParameter_localWithTypeParameter() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/recursiveTypeParameter_localWithTypeParameter.kt");
  }

  @Test
  @TestMetadata("typeParamFlexibleUpperBound.kt")
  public void testTypeParamFlexibleUpperBound() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/typeParamFlexibleUpperBound.kt");
  }

  @Test
  @TestMetadata("unitType.kt")
  public void testUnitType() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/unitType.kt");
  }

  @Test
  @TestMetadata("unitTypeNullable.kt")
  public void testUnitTypeNullable() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/unitTypeNullable.kt");
  }

  @Test
  @TestMetadata("unitTypeTypealias.kt")
  public void testUnitTypeTypealias() {
    runTest("analysis/analysis-api/testData/components/javaInteroperabilityComponent/asPsiType/forExpression/unitTypeTypealias.kt");
  }
}
