package com.example.processor.utils

import com.example.annotation.BundleThis
import com.example.processor.ModelClassField
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import javax.lang.model.element.Element

val RAW_BUNDLE_TYPE = mapOf(
    "byte" to "Byte",
    "java.lang.Byte" to "Byte",
    "kotlin.Byte" to "Byte",
    "short" to "Short",
    "java.lang.Short" to "Short",
    "kotlin.Short" to "Short",
    "int" to "Int",
    "java.lang.Integer" to "Int",
    "kotlin.Int" to "Int",
    "long" to "Long",
    "java.lang.Long" to "Long",
    "kotlin.Long" to "Long",
    "double" to "Double",
    "java.lang.Double" to "Double",
    "kotlin.Double" to "Double",
    "float" to "Float",
    "java.lang.Float" to "Float",
    "kotlin.Float" to "Float",
    "char" to "Char",
    "java.lang.Character" to "Char",
    "kotlin.Char" to "Char",
    "boolean" to "Boolean",
    "java.lang.Boolean" to "Boolean",
    "kotlin.Boolean" to "Boolean",
    "java.lang.String" to "String",
    "kotlin.String" to "String"
)

val RAW_LIST_BUNDLE_TYPE = mapOf(
    "int" to "IntegerArrayList",
    "java.lang.Integer" to "IntegerArrayList",
    "kotlin.Int" to "IntegerArrayList",
    "java.lang.String" to "StringArrayList",
    "kotlin.String" to "StringArrayList"
)

val CAST = mapOf(
    "byte" to "byte",
    "java.lang.Byte" to "byte",
    "kotlin.Byte" to "byte",
    "short" to "short",
    "java.lang.Short" to "short",
    "kotlin.Short" to "short",
    "int" to "int",
    "java.lang.Integer" to "int",
    "kotlin.Int" to "int",
    "long" to "long",
    "java.lang.Long" to "long",
    "kotlin.Long" to "long",
    "double" to "double",
    "java.lang.Double" to "double",
    "kotlin.Double" to "double",
    "float" to "float",
    "java.lang.Float" to "float",
    "kotlin.Float" to "float",
    "char" to "char",
    "java.lang.Character" to "char",
    "kotlin.Char" to "char",
    "boolean" to "boolean",
    "java.lang.Boolean" to "boolean",
    "kotlin.Boolean" to "boolean",
    "java.lang.String" to "String",
    "kotlin.String" to "String"
)

val LIST_TYPE = listOf(
    "java.util.List",
    "java.util.ArrayList"
)
val SET_TYPE = listOf(
    "java.util.Set",
    "java.util.HashSet"
)
val MAP_TYPE = listOf(
    "java.util.Map",
    "kotlin.collections.Map",
    "java.util.HashMap",
    "kotlin.collections.HashMap"
)

fun isRawType(typeName: TypeName) =
    RAW_BUNDLE_TYPE.containsKey(typeName.toString())

fun isMap(typeName: TypeName): Boolean {
    try {
        return MAP_TYPE.contains((typeName as ParameterizedTypeName).rawType.toString())
    } catch (ignored: ClassCastException) {
    }
    return false
}

fun isList(typeName: TypeName): Boolean {
    try {
        return LIST_TYPE.contains((typeName as ParameterizedTypeName).rawType.toString())
    } catch (ignored: ClassCastException) {
    }
    return false
}

fun isSet(typeName: TypeName): Boolean {
    try {
        return SET_TYPE.contains((typeName as ParameterizedTypeName).rawType.toString())
    } catch (ignored: ClassCastException) {
    }
    return false
}

fun getOwner(field: ModelClassField) =
    (field.element as Symbol).owner.toString()

fun isBundleable(element: Element) =
    (element as Symbol).asType().asElement().getAnnotation(BundleThis::class.java) != null

fun isParcelable(type: Element): Boolean {
    (type.asType() as Type.ClassType).interfaces_field?.forEach {
        if (it.asTypeName().toString() == "android.os.Parcelable") {
            return true
        }
    }
    return false
}

