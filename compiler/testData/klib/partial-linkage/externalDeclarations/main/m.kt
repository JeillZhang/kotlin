import abitestutils.abiTest
import abitestutils.TestBuilder

fun box() = abiTest {
    val ecifaec = ExternalClassInheritedFromAbstractExternalClass()
    val ecifeiifoec = ExternalClassInheritedFromExternalInterfaceInheritedFromOpenExternalClass()

    expectSuccess("ExternalClassInheritedFromAbstractExternalClass.abstractFunction") { ecifaec.abstractFunction() }
    expectSuccess("ExternalClassInheritedFromAbstractExternalClass.removedAbstractFunction") { ecifaec.removedAbstractFunction() }

    expectRuntimeFailure("Calling abstract function AbstractExternalClass.addedAbstractFunction") { ecifaec.addedAbstractFunction() }
    expectSuccess("AbstractExternalClass.function") { ecifaec.function() }
    expectFailure(linkage("Function 'removedFunction' can not be called: No function found for symbol '/ExternalClassInheritedFromAbstractExternalClass.removedFunction'")) { ecifaec.callRemovedFunction() }
    expectSuccess("AbstractExternalClass.addedFunction") { ecifaec.addedFunction() }

    expectSuccess("OpenExternalClass.function") { ecifeiifoec.function() }
    expectSuccess("ExternalClassInheritedFromExternalInterfaceInheritedFromOpenExternalClass.abstractFunction") { ecifeiifoec.abstractFunction() }
}

private fun TestBuilder.expectRuntimeFailure(errorMessage: String, block: () -> Any) =
    expectFailure(custom { throwable -> throwable !is Exception && throwable.message == errorMessage }) { block() }
