// RUN_PIPELINE_TILL: FRONTEND
interface A
interface B : A

interface ListA : List<A>
interface ListB : List<B>

interface Z<<!INCONSISTENT_TYPE_PARAMETER_BOUNDS, INCONSISTENT_TYPE_PARAMETER_BOUNDS, INCONSISTENT_TYPE_PARAMETER_BOUNDS!>T<!>> where T : ListA, T : ListB

/* GENERATED_FIR_TAGS: interfaceDeclaration, typeConstraint, typeParameter */
