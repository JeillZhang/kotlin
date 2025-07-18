class A(var b: Byte) {
  fun c(d: Short)  = (b + d.toByte()).toInt().toChar()
}

fun box() : String {
    if(A(10.toByte()).c(20.toShort()) != 30.toByte().toInt().toChar()) return "plus failed"

    var x = 20.toByte()
    var y = 20.toByte()
    val foo = {
        x++
        ++x
    }

    if(++x != 21.toByte() || x++ != 21.toByte() || foo() != 24.toByte() || x != 24.toByte()) return "shared byte fail"
    if(++y != 21.toByte() || y++ != 21.toByte() || y != 22.toByte()) return "byte fail"

    var xs = 20.toShort()
    var ys = 20.toShort()
    val foos = {
        xs++
        ++xs
    }

    if(++xs != 21.toShort() || xs++ != 21.toShort() || foos() != 24.toShort() || xs != 24.toShort()) return "shared short fail"
    if(++ys != 21.toShort() || ys++ != 21.toShort() || ys != 22.toShort()) return "short fail"
 
    var xc = 20.toInt().toChar()
    var yc = 20.toInt().toChar()
    val fooc = {
        xc++
        ++xc
    }

    if(++xc != 21.toInt().toChar() || xc++ != 21.toInt().toChar() || fooc() != 24.toInt().toChar() || xc != 24.toInt().toChar()) return "shared char fail"
    if(++yc != 21.toInt().toChar() || yc++ != 21.toInt().toChar() || yc != 22.toInt().toChar()) return "char fail"
 
    return "OK"
}
