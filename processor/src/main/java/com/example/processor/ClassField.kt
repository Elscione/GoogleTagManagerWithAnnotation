package com.example.processor

import com.example.annotation.BundleKey
import com.example.annotation.Default
import com.example.annotation.defaultvalue.*
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.annotations.Nullable
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement

class ClassField(
    val element: VariableElement,
    val default: Boolean,
    val defaultValue: Any?,
    val isNullable: Boolean
) {
    companion object {
        val DEFAULT_VALUE = mapOf(
            "java.lang.String" to "",
            "kotlin.String" to "",
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
            "kotlin.Char" to '\u0000',
            "java.lang.Character" to '\u0000'
        )

        fun getClassFields(clazz: AnnotatedClass): Map<String, ClassField> {
            val fields = mutableMapOf<String, ClassField>()
            val elements = clazz.element.enclosedElements

            elements.forEach {
                if (it.kind == ElementKind.FIELD && (clazz.nameAsKey || isElementKeyDefined(it as VariableElement))) {
                    val defaultAnnotation = it.getAnnotation(Default::class.java)
                    val defaultValue = getDefaultValue(it as VariableElement, defaultAnnotation, clazz.defaultAll)
                    val isNullable = isElementNullable(it)
                    val key = getKey(it, clazz.nameAsKey)

                    if (isElementValid(isNullable, defaultAnnotation, defaultValue)) {
                        fields[key!!] = ClassField(it, isDefault(defaultAnnotation, clazz.defaultAll), defaultValue, isNullable)
                    } else {
                        throw Exception("Property ${it.simpleName} on class ${clazz.getClassName()} must have default value")
                    }
                }
            }

            return fields
        }

        private fun getKey(it: VariableElement, nameAsKey: Boolean): String? {
            if (nameAsKey) {
                return it.simpleName.toString()
            } else {
                if (it.getAnnotation(BundleKey::class.java) != null) {
                    return it.getAnnotation(BundleKey::class.java).key
                } else {
                    return null
                }
            }
        }

        private fun isDefault(defaultAnnotation: Default?, defaultAll: Boolean) = defaultAnnotation?.default ?: defaultAll

        private fun isElementValid(nullable: Boolean, defaultAnnotation: Default?, defaultValue: Any?) = !(nullable && defaultAnnotation == null && defaultValue == null)

        private fun isElementNullable(it: VariableElement) = it.getAnnotation(Nullable::class.java) != null

        private fun getDefaultValue(it: VariableElement, defaultAnnotation: Default?, defaultAll: Boolean) = when {
            it.getAnnotation(DefaultValueByte::class.java) != null -> it.getAnnotation(DefaultValueByte::class.java).value
            it.getAnnotation(DefaultValueBoolean::class.java) != null -> it.getAnnotation(DefaultValueBoolean::class.java).value
            it.getAnnotation(DefaultValueShort::class.java) != null -> it.getAnnotation(DefaultValueShort::class.java).value
            it.getAnnotation(DefaultValueInt::class.java) != null -> it.getAnnotation(DefaultValueInt::class.java).value
            it.getAnnotation(DefaultValueFloat::class.java) != null -> it.getAnnotation(DefaultValueFloat::class.java).value
            it.getAnnotation(DefaultValueDouble::class.java) != null -> it.getAnnotation(DefaultValueDouble::class.java).value
            it.getAnnotation(DefaultValueChar::class.java) != null -> it.getAnnotation(DefaultValueChar::class.java).value
            it.getAnnotation(DefaultValueLong::class.java) != null -> it.getAnnotation(DefaultValueLong::class.java).value
            it.getAnnotation(DefaultValueString::class.java) != null -> it.getAnnotation(DefaultValueString::class.java).value
            else -> getDefaultValueBasedOnType(it, defaultAnnotation, defaultAll)
        }

        private fun getDefaultValueBasedOnType(it: VariableElement, defaultAnnotation: Default?, defaultAll: Boolean) =
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

        private fun isElementKeyDefined(it: VariableElement) = it.getAnnotation(BundleKey::class.java) != null
    }

}