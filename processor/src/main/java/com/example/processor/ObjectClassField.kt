package com.example.processor

import com.example.annotation.BundleThis
import com.example.annotation.Default
import com.example.annotation.Key
import com.example.annotation.defaultvalue.*
import com.example.processor.utils.FieldType
import com.squareup.javapoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Type
import javax.annotation.Nullable
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement

class ObjectClassField(
    val element: VariableElement,
    val default: Boolean,
    val type: FieldType,
    val defaultValue: Any?,
    val isNullable: Boolean

) {

    companion object {

        val DEFAULT_VALUE = mapOf(
            "java.lang.String" to "\"\"",
            "kotlin.String" to "\"\"",
            "kotlin.Byte" to 0,
            "java.lang.Byte" to 0,
            "kotlin.Short" to 0,
            "java.lang.Short" to 0,
            "kotlin.Int" to 0,
            "java.lang.Integer" to 0,
            "kotlin.Long" to 0L,
            "java.lang.Long" to 0L,
            "kotlin.Double" to 0.0,
            "java.lang.Double" to 0.0,
            "kotlin.Float" to 0.0f,
            "java.lang.Float" to 0.0f,
            "kotlin.Boolean" to false,
            "java.lang.Boolean" to false,
            "kotlin.Char" to "\'\u0000\'",
            "java.lang.Character" to "\'\u0000\'",
            "java.util.ArrayList" to "listOf<Any>()"
        )

        val BUNDLE_TYPE = mapOf(
            "java.lang.String" to "String",
            "kotlin.String" to "String",
            "kotlin.Byte" to "Byte",
            "java.lang.Byte" to "Byte",
            "kotlin.Short" to "Short",
            "java.lang.Short" to "Short",
            "kotlin.Int" to "Int",
            "java.lang.Integer" to "Int",
            "kotlin.Long" to "Long",
            "java.lang.Long" to "Long",
            "kotlin.Double" to "Double",
            "java.lang.Double" to "Double",
            "kotlin.Float" to "Float",
            "java.lang.Float" to "Float",
            "kotlin.Boolean" to "Boolean",
            "java.lang.Boolean" to "Boolean",
            "kotlin.Char" to "Char",
            "java.lang.Character" to "Char",
            "java.util.ArrayList" to "ParcelableArrayList"
        )

        fun getClassFields(clazz: AnnotatedObjectClass): Map<String, ObjectClassField> {

            val fields = mutableMapOf<String, ObjectClassField>()
            val elements = clazz.element.enclosedElements

            elements.forEach {
                if (it.kind == ElementKind.FIELD && clazz.nameAsKey) {

                    val defaultAnnotation = it.getAnnotation(Default::class.java)

                    val key = getKey(it as VariableElement, clazz.nameAsKey)
                    val isNullable = isElementNullable(it)

                    val defaultValue = getDefaultValue(
                        it,
                        defaultAnnotation,
                        clazz.defaultAll
                    )



                    if (key != null) {

                        val isPrimitive = it.asType().kind.isPrimitive || isNullable
                        val isString = it.asType().toString() != "java.lang.String"


//                        if (!isPrimitive && isString) {
                            if (isParcelable(it)) {
                                fields[key] = ObjectClassField(
                                    it,
                                    isDefault(defaultAnnotation, clazz.defaultAll),
                                    FieldType.Parcelable,
                                    defaultValue,
                                    isNullable
                                )
                            } else {
                                fields[key] = ObjectClassField(
                                    it,
                                    isDefault(defaultAnnotation, clazz.defaultAll),

                                    FieldType.Bundle, defaultValue,
                                    isNullable
                                )
//                            }
                        }
                    }
                }
            }

            return fields
        }

        private fun getDefaultValue(
            it: VariableElement,
            defaultAnnotation: Default?,
            defaultAll: Boolean
        ) = when {
            it.getAnnotation(DefaultValueByte::class.java) != null -> it.getAnnotation(
                DefaultValueByte::class.java
            ).value
            it.getAnnotation(DefaultValueBoolean::class.java) != null -> it.getAnnotation(
                DefaultValueBoolean::class.java
            ).value
            it.getAnnotation(DefaultValueShort::class.java) != null -> it.getAnnotation(
                DefaultValueShort::class.java
            ).value
            it.getAnnotation(DefaultValueInt::class.java) != null -> it.getAnnotation(
                DefaultValueInt::class.java
            ).value
            it.getAnnotation(DefaultValueFloat::class.java) != null -> it.getAnnotation(
                DefaultValueFloat::class.java
            ).value
            it.getAnnotation(DefaultValueDouble::class.java) != null -> it.getAnnotation(
                DefaultValueDouble::class.java
            ).value
            it.getAnnotation(DefaultValueChar::class.java) != null -> "\'${it.getAnnotation(
                DefaultValueChar::class.java
            ).value}\'"
            it.getAnnotation(DefaultValueLong::class.java) != null -> it.getAnnotation(
                DefaultValueLong::class.java
            ).value
            it.getAnnotation(DefaultValueString::class.java) != null -> "\"${it.getAnnotation(
                DefaultValueString::class.java
            ).value}\""
            else -> getDefaultValueBasedOnType(it, defaultAnnotation, defaultAll)
        }

        private fun getDefaultValueBasedOnType(
            it: VariableElement,
            defaultAnnotation: Default?,
            defaultAll: Boolean
        ) =
            if (defaultAll) {
                if (defaultAnnotation != null && !defaultAnnotation.default) {
                    null
                } else {
                    DEFAULT_VALUE[it.asType().asTypeName().toString()]
                }
            } else {
                if (defaultAnnotation != null && defaultAnnotation.default) {
                    DEFAULT_VALUE[it.asType().asTypeName().toString()]
                } else {
                    null
                }
            }

        private fun isElementNullable(it: VariableElement) =
            it.getAnnotation(Nullable::class.java) != null

        private fun getKey(it: VariableElement, nameAsKey: Boolean): String? {
            if (nameAsKey) {
                return it.simpleName.toString()
            } else {
                if (it.getAnnotation(Key::class.java) != null) {
                    return it.getAnnotation(Key::class.java).key
                } else {
                    return null
                }
            }
        }

        private fun isDefault(defaultAnnotation: Default?, defaultAll: Boolean) =
            defaultAnnotation?.default ?: defaultAll

        private fun isParcelable(element: Element): Boolean {

            (element.asType() as Type.ClassType).interfaces_field?.forEach {
                if (it.asTypeName().toString().equals("android.os.Parcelable")) {
                    return true
                }
            }

            return false
        }

        private fun isPrimitive(typeName: TypeName): Boolean {
            val clazz = getClassName(typeName)
            return clazz.isPrimitive || clazz.isBoxedPrimitive
        }

        private fun getClassName(typeName: TypeName): ClassName {
            val splitFqName = typeName.toString().split(".")
            val className = splitFqName.last()
            val pack = splitFqName.take(splitFqName.size - 1).joinToString(".")
            return ClassName.get(pack, className)
        }

        private fun isElementKeyDefined(it: VariableElement) =
            it.getAnnotation(Key::class.java) != null
    }

}