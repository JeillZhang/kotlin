

package org.jetbrains.kotlin.fir.dataframe;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.fir.dataframe.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("testData/box")
@TestDataPath("$PROJECT_ROOT")
public class DataFrameBlackBoxCodegenTestGenerated extends AbstractDataFrameBlackBoxCodegenTest {
  @Test
  @TestMetadata("addDsl.kt")
  public void testAddDsl() {
    runTest("testData/box/addDsl.kt");
  }

  @Test
  @TestMetadata("addId.kt")
  public void testAddId() {
    runTest("testData/box/addId.kt");
  }

  @Test
  public void testAllFilesPresentInBox() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("testData/box"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.JVM_IR, true);
  }

  @Test
  @TestMetadata("castTo.kt")
  public void testCastTo() {
    runTest("testData/box/castTo.kt");
  }

  @Test
  @TestMetadata("castTo_function.kt")
  public void testCastTo_function() {
    runTest("testData/box/castTo_function.kt");
  }

  @Test
  @TestMetadata("columnGroupApi.kt")
  public void testColumnGroupApi() {
    runTest("testData/box/columnGroupApi.kt");
  }

  @Test
  @TestMetadata("columnName.kt")
  public void testColumnName() {
    runTest("testData/box/columnName.kt");
  }

  @Test
  @TestMetadata("columnName_invalidSymbol.kt")
  public void testColumnName_invalidSymbol() {
    runTest("testData/box/columnName_invalidSymbol.kt");
  }

  @Test
  @TestMetadata("columnWithStarProjection.kt")
  public void testColumnWithStarProjection() {
    runTest("testData/box/columnWithStarProjection.kt");
  }

  @Test
  @TestMetadata("conflictingJvmDeclarations.kt")
  public void testConflictingJvmDeclarations() {
    runTest("testData/box/conflictingJvmDeclarations.kt");
  }

  @Test
  @TestMetadata("convertToDataFrame.kt")
  public void testConvertToDataFrame() {
    runTest("testData/box/convertToDataFrame.kt");
  }

  @Test
  @TestMetadata("convert_to.kt")
  public void testConvert_to() {
    runTest("testData/box/convert_to.kt");
  }

  @Test
  @TestMetadata("dataFrameOf.kt")
  public void testDataFrameOf() {
    runTest("testData/box/dataFrameOf.kt");
  }

  @Test
  @TestMetadata("dataFrameOf_to.kt")
  public void testDataFrameOf_to() {
    runTest("testData/box/dataFrameOf_to.kt");
  }

  @Test
  @TestMetadata("dataFrameOf_vararg.kt")
  public void testDataFrameOf_vararg() {
    runTest("testData/box/dataFrameOf_vararg.kt");
  }

  @Test
  @TestMetadata("dataRowSchemaApi.kt")
  public void testDataRowSchemaApi() {
    runTest("testData/box/dataRowSchemaApi.kt");
  }

  @Test
  @TestMetadata("dataSchemaCodegen.kt")
  public void testDataSchemaCodegen() {
    runTest("testData/box/dataSchemaCodegen.kt");
  }

  @Test
  @TestMetadata("dfIde.kt")
  public void testDfIde() {
    runTest("testData/box/dfIde.kt");
  }

  @Test
  @TestMetadata("diff.kt")
  public void testDiff() {
    runTest("testData/box/diff.kt");
  }

  @Test
  @TestMetadata("dropNA.kt")
  public void testDropNA() {
    runTest("testData/box/dropNA.kt");
  }

  @Test
  @TestMetadata("dropNulls.kt")
  public void testDropNulls() {
    runTest("testData/box/dropNulls.kt");
  }

  @Test
  @TestMetadata("duplicatedSignature.kt")
  public void testDuplicatedSignature() {
    runTest("testData/box/duplicatedSignature.kt");
  }

  @Test
  @TestMetadata("duplicatedSignature1.kt")
  public void testDuplicatedSignature1() {
    runTest("testData/box/duplicatedSignature1.kt");
  }

  @Test
  @TestMetadata("explode.kt")
  public void testExplode() {
    runTest("testData/box/explode.kt");
  }

  @Test
  @TestMetadata("explodeDataFrame.kt")
  public void testExplodeDataFrame() {
    runTest("testData/box/explodeDataFrame.kt");
  }

  @Test
  @TestMetadata("extractDataSchemaWithStarProjection.kt")
  public void testExtractDataSchemaWithStarProjection() {
    runTest("testData/box/extractDataSchemaWithStarProjection.kt");
  }

  @Test
  @TestMetadata("extractDataSchemaWithTypeParameter.kt")
  public void testExtractDataSchemaWithTypeParameter() {
    runTest("testData/box/extractDataSchemaWithTypeParameter.kt");
  }

  @Test
  @TestMetadata("extractPluginSchemaWithUnfold.kt")
  public void testExtractPluginSchemaWithUnfold() {
    runTest("testData/box/extractPluginSchemaWithUnfold.kt");
  }

  @Test
  @TestMetadata("fillNulls.kt")
  public void testFillNulls() {
    runTest("testData/box/fillNulls.kt");
  }

  @Test
  @TestMetadata("flatten.kt")
  public void testFlatten() {
    runTest("testData/box/flatten.kt");
  }

  @Test
  @TestMetadata("flexibleReturnType.kt")
  public void testFlexibleReturnType() {
    runTest("testData/box/flexibleReturnType.kt");
  }

  @Test
  @TestMetadata("group.kt")
  public void testGroup() {
    runTest("testData/box/group.kt");
  }

  @Test
  @TestMetadata("groupBy.kt")
  public void testGroupBy() {
    runTest("testData/box/groupBy.kt");
  }

  @Test
  @TestMetadata("groupByAdd.kt")
  public void testGroupByAdd() {
    runTest("testData/box/groupByAdd.kt");
  }

  @Test
  @TestMetadata("groupBy_DataRow.kt")
  public void testGroupBy_DataRow() {
    runTest("testData/box/groupBy_DataRow.kt");
  }

  @Test
  @TestMetadata("groupBy_count.kt")
  public void testGroupBy_count() {
    runTest("testData/box/groupBy_count.kt");
  }

  @Test
  @TestMetadata("groupBy_extractSchema.kt")
  public void testGroupBy_extractSchema() {
    runTest("testData/box/groupBy_extractSchema.kt");
  }

  @Test
  @TestMetadata("groupBy_maxOfMinOf.kt")
  public void testGroupBy_maxOfMinOf() {
    runTest("testData/box/groupBy_maxOfMinOf.kt");
  }

  @Test
  @TestMetadata("groupBy_refine.kt")
  public void testGroupBy_refine() {
    runTest("testData/box/groupBy_refine.kt");
  }

  @Test
  @TestMetadata("groupBy_toDataFrame.kt")
  public void testGroupBy_toDataFrame() {
    runTest("testData/box/groupBy_toDataFrame.kt");
  }

  @Test
  @TestMetadata("infer.kt")
  public void testInfer() {
    runTest("testData/box/infer.kt");
  }

  @Test
  @TestMetadata("injectAccessors.kt")
  public void testInjectAccessors() {
    runTest("testData/box/injectAccessors.kt");
  }

  @Test
  @TestMetadata("injectAccessorsDsl.kt")
  public void testInjectAccessorsDsl() {
    runTest("testData/box/injectAccessorsDsl.kt");
  }

  @Test
  @TestMetadata("insert.kt")
  public void testInsert() {
    runTest("testData/box/insert.kt");
  }

  @Test
  @TestMetadata("inventNamesForLocalClasses.kt")
  public void testInventNamesForLocalClasses() {
    runTest("testData/box/inventNamesForLocalClasses.kt");
  }

  @Test
  @TestMetadata("join.kt")
  public void testJoin() {
    runTest("testData/box/join.kt");
  }

  @Test
  @TestMetadata("join_1.kt")
  public void testJoin_1() {
    runTest("testData/box/join_1.kt");
  }

  @Test
  @TestMetadata("localTypeExposure.kt")
  public void testLocalTypeExposure() {
    runTest("testData/box/localTypeExposure.kt");
  }

  @Test
  @TestMetadata("lowerGeneratedImplicitReceiver.kt")
  public void testLowerGeneratedImplicitReceiver() {
    runTest("testData/box/lowerGeneratedImplicitReceiver.kt");
  }

  @Test
  @TestMetadata("main.kt")
  public void testMain() {
    runTest("testData/box/main.kt");
  }

  @Test
  @TestMetadata("mapToFrame.kt")
  public void testMapToFrame() {
    runTest("testData/box/mapToFrame.kt");
  }

  @Test
  @TestMetadata("merge.kt")
  public void testMerge() {
    runTest("testData/box/merge.kt");
  }

  @Test
  @TestMetadata("modifySchemaInAggregate.kt")
  public void testModifySchemaInAggregate() {
    runTest("testData/box/modifySchemaInAggregate.kt");
  }

  @Test
  @TestMetadata("moveAfter.kt")
  public void testMoveAfter() {
    runTest("testData/box/moveAfter.kt");
  }

  @Test
  @TestMetadata("moveInto.kt")
  public void testMoveInto() {
    runTest("testData/box/moveInto.kt");
  }

  @Test
  @TestMetadata("moveToLeft.kt")
  public void testMoveToLeft() {
    runTest("testData/box/moveToLeft.kt");
  }

  @Test
  @TestMetadata("moveToRight.kt")
  public void testMoveToRight() {
    runTest("testData/box/moveToRight.kt");
  }

  @Test
  @TestMetadata("moveToTop.kt")
  public void testMoveToTop() {
    runTest("testData/box/moveToTop.kt");
  }

  @Test
  @TestMetadata("moveUnder.kt")
  public void testMoveUnder() {
    runTest("testData/box/moveUnder.kt");
  }

  @Test
  @TestMetadata("nestedDataSchemaCodegen.kt")
  public void testNestedDataSchemaCodegen() {
    runTest("testData/box/nestedDataSchemaCodegen.kt");
  }

  @Test
  @TestMetadata("OuterClass.kt")
  public void testOuterClass() {
    runTest("testData/box/OuterClass.kt");
  }

  @Test
  @TestMetadata("parametrizedDataFrame.kt")
  public void testParametrizedDataFrame() {
    runTest("testData/box/parametrizedDataFrame.kt");
  }

  @Test
  @TestMetadata("perRowCol.kt")
  public void testPerRowCol() {
    runTest("testData/box/perRowCol.kt");
  }

  @Test
  @TestMetadata("platformType.kt")
  public void testPlatformType() {
    runTest("testData/box/platformType.kt");
  }

  @Test
  @TestMetadata("playground.kt")
  public void testPlayground() {
    runTest("testData/box/playground.kt");
  }

  @Test
  @TestMetadata("propertiesOrder.kt")
  public void testPropertiesOrder() {
    runTest("testData/box/propertiesOrder.kt");
  }

  @Test
  @TestMetadata("read.kt")
  public void testRead() {
    runTest("testData/box/read.kt");
  }

  @Test
  @TestMetadata("readCSV.kt")
  public void testReadCSV() {
    runTest("testData/box/readCSV.kt");
  }

  @Test
  @TestMetadata("readExcel.kt")
  public void testReadExcel() {
    runTest("testData/box/readExcel.kt");
  }

  @Test
  @TestMetadata("readExcel_stringColumns.kt")
  public void testReadExcel_stringColumns() {
    runTest("testData/box/readExcel_stringColumns.kt");
  }

  @Test
  @TestMetadata("readJson.kt")
  public void testReadJson() {
    runTest("testData/box/readJson.kt");
  }

  @Test
  @TestMetadata("readJsonStr_const.kt")
  public void testReadJsonStr_const() {
    runTest("testData/box/readJsonStr_const.kt");
  }

  @Test
  @TestMetadata("readJsonStr_datarow.kt")
  public void testReadJsonStr_datarow() {
    runTest("testData/box/readJsonStr_datarow.kt");
  }

  @Test
  @TestMetadata("readJsonStr_localProperty.kt")
  public void testReadJsonStr_localProperty() {
    runTest("testData/box/readJsonStr_localProperty.kt");
  }

  @Test
  @TestMetadata("readJsonStr_memberProperty.kt")
  public void testReadJsonStr_memberProperty() {
    runTest("testData/box/readJsonStr_memberProperty.kt");
  }

  @Test
  @TestMetadata("read_localFile.kt")
  public void testRead_localFile() {
    runTest("testData/box/read_localFile.kt");
  }

  @Test
  @TestMetadata("reducedGroupBy.kt")
  public void testReducedGroupBy() {
    runTest("testData/box/reducedGroupBy.kt");
  }

  @Test
  @TestMetadata("remove.kt")
  public void testRemove() {
    runTest("testData/box/remove.kt");
  }

  @Test
  @TestMetadata("rename.kt")
  public void testRename() {
    runTest("testData/box/rename.kt");
  }

  @Test
  @TestMetadata("renameMapping.kt")
  public void testRenameMapping() {
    runTest("testData/box/renameMapping.kt");
  }

  @Test
  @TestMetadata("renameToCamelCase.kt")
  public void testRenameToCamelCase() {
    runTest("testData/box/renameToCamelCase.kt");
  }

  @Test
  @TestMetadata("reorder.kt")
  public void testReorder() {
    runTest("testData/box/reorder.kt");
  }

  @Test
  @TestMetadata("Schema.kt")
  public void testSchema() {
    runTest("testData/box/Schema.kt");
  }

  @Test
  @TestMetadata("schemaFromImplicitReceiver.kt")
  public void testSchemaFromImplicitReceiver() {
    runTest("testData/box/schemaFromImplicitReceiver.kt");
  }

  @Test
  @TestMetadata("select.kt")
  public void testSelect() {
    runTest("testData/box/select.kt");
  }

  @Test
  @TestMetadata("selectColsOf.kt")
  public void testSelectColsOf() {
    runTest("testData/box/selectColsOf.kt");
  }

  @Test
  @TestMetadata("selectIt.kt")
  public void testSelectIt() {
    runTest("testData/box/selectIt.kt");
  }

  @Test
  @TestMetadata("selectThis.kt")
  public void testSelectThis() {
    runTest("testData/box/selectThis.kt");
  }

  @Test
  @TestMetadata("selectionDsl.kt")
  public void testSelectionDsl() {
    runTest("testData/box/selectionDsl.kt");
  }

  @Test
  @TestMetadata("toDataFrame.kt")
  public void testToDataFrame() {
    runTest("testData/box/toDataFrame.kt");
  }

  @Test
  @TestMetadata("toDataFrame_column.kt")
  public void testToDataFrame_column() {
    runTest("testData/box/toDataFrame_column.kt");
  }

  @Test
  @TestMetadata("toDataFrame_customIterable.kt")
  public void testToDataFrame_customIterable() {
    runTest("testData/box/toDataFrame_customIterable.kt");
  }

  @Test
  @TestMetadata("toDataFrame_dataSchema.kt")
  public void testToDataFrame_dataSchema() {
    runTest("testData/box/toDataFrame_dataSchema.kt");
  }

  @Test
  @TestMetadata("toDataFrame_dsl.kt")
  public void testToDataFrame_dsl() {
    runTest("testData/box/toDataFrame_dsl.kt");
  }

  @Test
  @TestMetadata("toDataFrame_from.kt")
  public void testToDataFrame_from() {
    runTest("testData/box/toDataFrame_from.kt");
  }

  @Test
  @TestMetadata("toDataFrame_local_class.kt")
  public void testToDataFrame_local_class() {
    runTest("testData/box/toDataFrame_local_class.kt");
  }

  @Test
  @TestMetadata("toDataFrame_nested.kt")
  public void testToDataFrame_nested() {
    runTest("testData/box/toDataFrame_nested.kt");
  }

  @Test
  @TestMetadata("toDataFrame_nullableList.kt")
  public void testToDataFrame_nullableList() {
    runTest("testData/box/toDataFrame_nullableList.kt");
  }

  @Test
  @TestMetadata("toDataFrame_nullableListSubtree.kt")
  public void testToDataFrame_nullableListSubtree() {
    runTest("testData/box/toDataFrame_nullableListSubtree.kt");
  }

  @Test
  @TestMetadata("toDataFrame_nullableSubtree.kt")
  public void testToDataFrame_nullableSubtree() {
    runTest("testData/box/toDataFrame_nullableSubtree.kt");
  }

  @Test
  @TestMetadata("toDataFrame_private_class.kt")
  public void testToDataFrame_private_class() {
    runTest("testData/box/toDataFrame_private_class.kt");
  }

  @Test
  @TestMetadata("toDataFrame_private_properties.kt")
  public void testToDataFrame_private_properties() {
    runTest("testData/box/toDataFrame_private_properties.kt");
  }

  @Test
  @TestMetadata("toDataFrame_superType.kt")
  public void testToDataFrame_superType() {
    runTest("testData/box/toDataFrame_superType.kt");
  }

  @Test
  @TestMetadata("toDataFrame_typeParameters.kt")
  public void testToDataFrame_typeParameters() {
    runTest("testData/box/toDataFrame_typeParameters.kt");
  }

  @Test
  @TestMetadata("transformReplaceFunctionCall.kt")
  public void testTransformReplaceFunctionCall() {
    runTest("testData/box/transformReplaceFunctionCall.kt");
  }

  @Test
  @TestMetadata("trimIndent.kt")
  public void testTrimIndent() {
    runTest("testData/box/trimIndent.kt");
  }

  @Test
  @TestMetadata("trimMargin.kt")
  public void testTrimMargin() {
    runTest("testData/box/trimMargin.kt");
  }

  @Test
  @TestMetadata("ungroup.kt")
  public void testUngroup() {
    runTest("testData/box/ungroup.kt");
  }

  @Test
  @TestMetadata("update.kt")
  public void testUpdate() {
    runTest("testData/box/update.kt");
  }

  @Test
  @TestMetadata("valueCounts.kt")
  public void testValueCounts() {
    runTest("testData/box/valueCounts.kt");
  }

  @Test
  @TestMetadata("wrongReceiver.kt")
  public void testWrongReceiver() {
    runTest("testData/box/wrongReceiver.kt");
  }

  @Nested
  @TestMetadata("testData/box/colKinds")
  @TestDataPath("$PROJECT_ROOT")
  public class ColKinds {
    @Test
    @TestMetadata("add.kt")
    public void testAdd() {
      runTest("testData/box/colKinds/add.kt");
    }

    @Test
    public void testAllFilesPresentInColKinds() {
      KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("testData/box/colKinds"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.JVM_IR, true);
    }

    @Test
    @TestMetadata("toDataFrame.kt")
    public void testToDataFrame() {
      runTest("testData/box/colKinds/toDataFrame.kt");
    }
  }
}
