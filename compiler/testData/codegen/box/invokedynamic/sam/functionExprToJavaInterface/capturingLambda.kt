// TARGET_BACKEND: JVM
// JVM_TARGET: 1.8
// SAM_CONVERSIONS: INDY
// LAMBDAS: INDY

// CHECK_BYTECODE_TEXT
// 2 java/lang/invoke/LambdaMetafactory

// FILE: capturingLambda.kt
fun box(): String {
    val co = 'O'
    val lambda = { co.toString() + "K" }
    return Sam(lambda).get()
}

// FILE: Sam.java
public interface Sam {
    String get();
}
