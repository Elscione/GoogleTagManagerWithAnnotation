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
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement

class ObjectClassField(
    val element: VariableElement,
    val type: FieldType,
    val defaultValue: Any?,
    val isNullable: Boolean

) {

    companion object {
        fun getClassFields(clazz: AnnotatedObjectClass): Map<String, ObjectClassField> {

            val fields = mutableMapOf<String, ObjectClassField>()
            val elements = clazz.element.enclosedElements

            elements.forEach {
                if (it.kind == ElementKind.FIELD && clazz.nameAsKey) {
                    val key = getKey(it as VariableElement, clazz.nameAsKey)

                    val isNullable = isElementNullable(it)
                    val defaultAnnotation = it.getAnnotation(Default::class.java)

                    val defaultValue =
                        getDefaultValue(
                            it,
                            defaultAnnotation,
                            clazz.defaultAll
                        )

                    if (key != null) {

                        if (isParcelable(it)) {
                            fields[key] = ObjectClassField(
                                it,
                                FieldType.Parcelable,
                                defaultValue,
                                isNullable
                            )
                        } else {
                            fields[key] = ObjectClassField(
                                it,
                                FieldType.Bundle, defaultValue,
                                isNullable
                            )
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
                    ModelClassField.DEFAULT_VALUE[it.asType().asTypeName().toString()]
                }
            } else {
                if (defaultAnnotation != null && defaultAnnotation.default) {
                    ModelClassField.DEFAULT_VALUE[it.asType().asTypeName().toString()]
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

        private fun isParcelable(it: VariableElement): Boolean {
            var implementParcelable = false

            (it.asType() as Type.ClassType).interfaces_field.forEach {
                if (it.asTypeName().toString().equals("android.os.Parcelable")) {
                    implementParcelable = true
                }
            }

            return implementParcelable
        }

        private fun isPrimitive(typeName: TypeName): Boolean {
            val clazz = getClassName(typeName)
            return clazz.isPrimitive || clazz.isBoxedPrimitive
        }

        private fun getClassName(typeName: TypeName): com.squareup.javapoet.ClassName {
            val splitFqName = typeName.toString().split(".")
            val className = splitFqName.last()
            val pack = splitFqName.take(splitFqName.size - 1).joinToString(".")
            return ClassName.get(pack, className)
        }
    }

}