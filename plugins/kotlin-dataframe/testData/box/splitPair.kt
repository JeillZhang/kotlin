import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

fun box(): String {
    val df = dataFrameOf("a" to listOf(1 to "2")).split { a }.into("i", "str")
    val col1: DataColumn<Int> = df.i
    val col2: DataColumn<String> = df.str
    df.assert()
    return "OK"
}
