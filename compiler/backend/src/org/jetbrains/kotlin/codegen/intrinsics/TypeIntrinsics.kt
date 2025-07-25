/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.builtins.StandardNames.FqNames
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.org.objectweb.asm.tree.*

object TypeIntrinsics {

    /**
     * Returns whether the generation of `is` type check for a given type would require use
     * of intrinsics rather than simple `instanceof`.
     *
     * Shall be in sync with `instanceOf(..)` below
     */
    @JvmStatic
    fun isIntrinsicRequiredForInstanceOf(kotlinType: KotlinType): Boolean =
        getFunctionTypeArity(kotlinType) >= 0 || getSuspendFunctionTypeArity(kotlinType) >= 0 ||
                getIsMutableCollectionMethodName(kotlinType) != null

    @JvmStatic
    fun instanceOf(v: InstructionAdapter, kotlinType: KotlinType, boxedAsmType: Type) {
        val functionTypeArity = getFunctionTypeArity(kotlinType)
        if (functionTypeArity >= 0) {
            v.iconst(functionTypeArity)
            v.typeIntrinsic(IS_FUNCTON_OF_ARITY_METHOD_NAME, IS_FUNCTON_OF_ARITY_DESCRIPTOR)
            return
        }

        val suspendFunctionTypeArity = getSuspendFunctionTypeArity(kotlinType)
        if (suspendFunctionTypeArity >= 0) {
            val notSuspendLambda = Label()
            val end = Label()

            with(v) {
                dup()
                instanceOf(AsmTypes.SUSPEND_FUNCTION_TYPE)
                ifeq(notSuspendLambda)
                iconst(suspendFunctionTypeArity + 1)
                typeIntrinsic(IS_FUNCTON_OF_ARITY_METHOD_NAME, IS_FUNCTON_OF_ARITY_DESCRIPTOR)
                goTo(end)

                mark(notSuspendLambda)
                pop()
                iconst(0)

                mark(end)
            }
            return
        }

        val isMutableCollectionMethodName = getIsMutableCollectionMethodName(kotlinType)
        if (isMutableCollectionMethodName != null) {
            v.typeIntrinsic(isMutableCollectionMethodName, IS_MUTABLE_COLLECTION_METHOD_DESCRIPTOR)
            return
        }

        v.instanceOf(boxedAsmType)
    }

    private fun iconstNode(value: Int): AbstractInsnNode =
            if (value >= -1 && value <= 5) {
                InsnNode(Opcodes.ICONST_0 + value)
            }
            else if (value >= java.lang.Byte.MIN_VALUE && value <= java.lang.Byte.MAX_VALUE) {
                IntInsnNode(Opcodes.BIPUSH, value)
            }
            else if (value >= java.lang.Short.MIN_VALUE && value <= java.lang.Short.MAX_VALUE) {
                IntInsnNode(Opcodes.SIPUSH, value)
            }
            else {
                LdcInsnNode(value)
            }

    @JvmStatic fun instanceOf(instanceofInsn: TypeInsnNode, instructions: InsnList, kotlinType: KotlinType, asmType: Type) {
        val functionTypeArity = getFunctionTypeArity(kotlinType)
        if (functionTypeArity >= 0) {
            instructions.insertBefore(instanceofInsn, iconstNode(functionTypeArity))
            instructions.insertBefore(instanceofInsn,
                                      typeIntrinsicNode(IS_FUNCTON_OF_ARITY_METHOD_NAME, IS_FUNCTON_OF_ARITY_DESCRIPTOR))
            instructions.remove(instanceofInsn)
            return
        }

        val isMutableCollectionMethodName = getIsMutableCollectionMethodName(kotlinType)
        if (isMutableCollectionMethodName != null) {
            instructions.insertBefore(instanceofInsn,
                                      typeIntrinsicNode(isMutableCollectionMethodName, IS_MUTABLE_COLLECTION_METHOD_DESCRIPTOR))
            instructions.remove(instanceofInsn)
            return
        }

        instanceofInsn.desc = asmType.internalName
    }

    @JvmStatic fun checkcast(
            v: InstructionAdapter,
            kotlinType: KotlinType, asmType: Type,
            // This parameter is just for sake of optimization:
            // when we generate 'as?' we do necessary intrinsic checks
            // when calling TypeIntrinsics.instanceOf, so here we can just make checkcast
            safe: Boolean) {
        if (safe) {
            v.checkcast(asmType)
            return
        }

        val functionTypeArity = getFunctionTypeArity(kotlinType)
        if (functionTypeArity >= 0) {
            v.iconst(functionTypeArity)
            v.typeIntrinsic(BEFORE_CHECKCAST_TO_FUNCTION_OF_ARITY, BEFORE_CHECKCAST_TO_FUNCTION_OF_ARITY_DESCRIPTOR)
            v.checkcast(asmType)
            return
        }

        val asMutableCollectionMethodName = getAsMutableCollectionMethodName(kotlinType)
        if (asMutableCollectionMethodName != null) {
            v.typeIntrinsic(asMutableCollectionMethodName, getAsMutableCollectionDescriptor(asmType))
            return
        }

        v.checkcast(asmType)
    }

    private val INTRINSICS_CLASS = "kotlin/jvm/internal/TypeIntrinsics"

    private val IS_FUNCTON_OF_ARITY_METHOD_NAME = "isFunctionOfArity"

    private val IS_FUNCTON_OF_ARITY_DESCRIPTOR =
            Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getObjectType("java/lang/Object"), Type.INT_TYPE)


    private val MUTABLE_COLLECTION_TYPE_FQ_NAMES = setOf(
        FqNames.mutableIterator,
        FqNames.mutableIterable,
        FqNames.mutableCollection,
        FqNames.mutableList,
        FqNames.mutableListIterator,
        FqNames.mutableMap,
        FqNames.mutableSet,
        FqNames.mutableMapEntry
    )

    private fun getMutableCollectionMethodName(prefix: String, kotlinType: KotlinType): String? {
        val fqName = getClassFqName(kotlinType)
        if (fqName == null || fqName !in MUTABLE_COLLECTION_TYPE_FQ_NAMES) return null
        val baseName = if (fqName == FqNames.mutableMapEntry) "MutableMapEntry" else fqName.shortName().asString()
        return prefix + baseName
    }

    private fun getIsMutableCollectionMethodName(kotlinType: KotlinType): String? = getMutableCollectionMethodName("is", kotlinType)

    private fun getAsMutableCollectionMethodName(kotlinType: KotlinType): String? = getMutableCollectionMethodName("as", kotlinType)

    private val IS_MUTABLE_COLLECTION_METHOD_DESCRIPTOR =
            Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getObjectType("java/lang/Object"))

    private fun getClassFqName(kotlinType: KotlinType): FqName? {
        val classDescriptor = TypeUtils.getClassDescriptor(kotlinType) ?: return null
        return DescriptorUtils.getFqName(classDescriptor).toSafe()
    }

    private val KOTLIN_FUNCTION_INTERFACE_REGEX = Regex("^kotlin\\.Function([0-9]+)$")
    private val KOTLIN_SUSPEND_FUNCTION_INTERFACE_REGEX = Regex("^kotlin\\.coroutines\\.SuspendFunction([0-9]+)$")

    /**
     * @return function type arity (non-negative), or -1 if the given type is not a function type
     */
    private fun getFunctionTypeArity(kotlinType: KotlinType): Int = getFunctionTypeArityByRegex(kotlinType, KOTLIN_FUNCTION_INTERFACE_REGEX)

    private fun getFunctionTypeArityByRegex(kotlinType: KotlinType, regex: Regex): Int {
        val classFqName = getClassFqName(kotlinType) ?: return -1
        val match = regex.find(classFqName.asString()) ?: return -1
        return Integer.valueOf(match.groups[1]!!.value)
    }

    /**
     * @return function type arity (non-negative, not counting continuation), or -1 if the given type is not a function type
     */
    private fun getSuspendFunctionTypeArity(kotlinType: KotlinType): Int =
        getFunctionTypeArityByRegex(kotlinType, KOTLIN_SUSPEND_FUNCTION_INTERFACE_REGEX)

    private fun typeIntrinsicNode(methodName: String, methodDescriptor: String): MethodInsnNode =
            MethodInsnNode(Opcodes.INVOKESTATIC, INTRINSICS_CLASS, methodName, methodDescriptor, false)

    private fun InstructionAdapter.typeIntrinsic(methodName: String, methodDescriptor: String) {
        invokestatic(INTRINSICS_CLASS, methodName, methodDescriptor, false)
    }


    private val OBJECT_TYPE = Type.getObjectType("java/lang/Object")

    private fun getAsMutableCollectionDescriptor(asmType: Type): String =
            Type.getMethodDescriptor(asmType, OBJECT_TYPE)

    private val BEFORE_CHECKCAST_TO_FUNCTION_OF_ARITY = "beforeCheckcastToFunctionOfArity"

    private val BEFORE_CHECKCAST_TO_FUNCTION_OF_ARITY_DESCRIPTOR =
            Type.getMethodDescriptor(OBJECT_TYPE, OBJECT_TYPE, Type.INT_TYPE)
}
