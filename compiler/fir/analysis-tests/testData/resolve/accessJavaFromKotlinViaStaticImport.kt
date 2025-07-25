// RUN_PIPELINE_TILL: BACKEND
// DISABLE_JAVA_FACADE
// FILE: useSite.kt

import InspectionProfileImpl.INIT_INSPECTIONS

fun foo(): Int = 4

// FILE: InspectionProfileImpl.java
import static Configuration.StaticConfigurationClass;

public class InspectionProfileImpl extends StaticConfigurationClass {
    public static boolean INIT_INSPECTIONS;
}

// FILE: Configuration.java
public class Configuration implements KotlinInterface {
    public static class StaticConfigurationClass {

    }
}

// FILE: KotlinInterface.kt
interface KotlinInterface {
    var selectedOptions: Int
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, interfaceDeclaration, propertyDeclaration */
