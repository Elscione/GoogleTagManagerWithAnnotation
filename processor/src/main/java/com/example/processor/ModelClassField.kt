package com.example.processor

import com.example.annotation.BundleThis
import com.example.annotation.Key
import com.example.annotation.Default
import com.example.annotation.defaultvalue.*
import com.example.processor.utils.*
import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import org.jetbrains.annotations.Nullable
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter

class ModelClassField(
    val element: VariableElement,
    val key: String,
    val defaultValue: Any?,
    val isNullable: Boolean
) {
    companion object {
        val keys = HashMap<String, HashMap<String, String>>()

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

        fun getClassFields(clazz: AnnotatedModelClass): Map<String, ModelClassField> {
            val fields = mutableMapOf<String, ModelClassField>()
            val elements = ElementFilter.fieldsIn(clazz.element.enclosedElements)

            val keySet = HashMap<String, String>()

            elements.forEach {
                if (it.kind == ElementKind.FIELD && (clazz.nameAsKey || isElementKeyDefined(it as VariableElement))) {
                    val defaultAnnotation = it.getAnnotation(Default::class.java)
                    val defaultValue =
                        getDefaultValue(it as VariableElement, defaultAnnotation, clazz.defaultAll)
                    val key = getKey(it, clazz.nameAsKey)

                    val isNullable = isElementNullable(it)

                    val fieldTypeName = com.squareup.javapoet.TypeName.get(it.asType())

                    if(it.simpleName.toString() == "CREATOR") {
                        return@forEach
                    }

                    if(!isRawType(fieldTypeName)) {
                        val type: Type
                        it as Symbol
                        if(isList(fieldTypeName) || isSet(fieldTypeName)) {
                            type = (it.asType() as Type.ClassType).typarams_field[0]
                        } else if(isMap(fieldTypeName)) {
                            type = (it.asType() as Type.ClassType).typarams_field[1]
                        } else {
                            type = it.asType()
                        }

                        if(!isBundleable(type.asElement()) && !isParcelable(type.asElement())) {
                            throw Exception("Can't found class ${BundleThis::class.java.simpleName}")
                        }
                    }

                    if (isElementValid(
                            isNullable,
                            defaultAnnotation,
                            defaultValue,
                            it,
                            clazz.nameAsKey
                        )
                    ) {
                        fields[key!!] = ModelClassField(
                            it,
                            key,
                            defaultValue,
                            isNullable
                        )

                        val ownerFqName = (it as Symbol).owner.toString()
                        AnnotationProcessor.foundParams.add("${ownerFqName}.${(it as VariableElement).simpleName}")
                        keySet[key] = it.asType().toString()
                    } else {
                        val name = it
                        throw Exception("Property ${name.simpleName} on class ${clazz.getClassName()} must have default value")
                    }
                }
            }

            keys.putIfAbsent((clazz.element as Symbol.ClassSymbol).fullname.toString(), keySet)

            return fields
        }

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

        private fun isDefault(defaultAnnotation: Default?, defaultAll: Boolean) = defaultAnnotation?.default ?: defaultAll

        private fun isElementValid(
            nullable: Boolean,
            defaultAnnotation: Default?,
            defaultValue: Any?,
            it: VariableElement,
            nameAsKey: Boolean
        ) = (!nullable || (defaultAnnotation != null || defaultValue != null)) && !hasDuplicateDefaultAnnotation(it, defaultAnnotation) && !hasDuplicateKey(it, nameAsKey)

        private fun hasDuplicateKey(it: VariableElement, nameAsKey: Boolean): Boolean {
            if (!nameAsKey) return false
            if (it.getAnnotation(Key::class.java) != null) {
                throw Exception("Property ${it.simpleName} has duplicate key definition")
            }

            return false
        }

        private fun hasDuplicateDefaultAnnotation(
            it: VariableElement,
            defaultAnnotation: Default?
        ): Boolean {
            if (defaultAnnotation == null) return false
            if (it.getAnnotation(DefaultValueString::class.java) != null ||
                it.getAnnotation(DefaultValueLong::class.java) != null ||
                it.getAnnotation(DefaultValueChar::class.java) != null ||
                it.getAnnotation(DefaultValueDouble::class.java) != null ||
                it.getAnnotation(DefaultValueFloat::class.java) != null ||
                it.getAnnotation(DefaultValueInt::class.java) != null ||
                it.getAnnotation(DefaultValueShort::class.java) != null ||
                it.getAnnotation(DefaultValueBoolean::class.java) != null ||
                it.getAnnotation(DefaultValueByte::class.java) != null) {
                throw Exception("Property ${it.simpleName} has two default value definition")
            }
            return false
        }

        private fun isElementNullable(it: VariableElement) = it.getAnnotation(Nullable::class.java) != null

        private fun getDefaultValue(it: VariableElement, defaultAnnotation: Default?, defaultAll: Boolean) = when {
            it.getAnnotation(DefaultValueByte::class.java) != null -> it.getAnnotation(DefaultValueByte::class.java).value
            it.getAnnotation(DefaultValueBoolean::class.java) != null -> it.getAnnotation(DefaultValueBoolean::class.java).value
            it.getAnnotation(DefaultValueShort::class.java) != null -> "\"${it.getAnnotation(DefaultValueShort::class.java).value}F\""
            it.getAnnotation(DefaultValueInt::class.java) != null -> it.getAnnotation(DefaultValueInt::class.java).value
            it.getAnnotation(DefaultValueFloat::class.java) != null -> it.getAnnotation(DefaultValueDouble::class.java).value
            it.getAnnotation(DefaultValueDouble::class.java) != null -> it.getAnnotation(DefaultValueDouble::class.java).value
            it.getAnnotation(DefaultValueChar::class.java) != null -> "\'${it.getAnnotation(DefaultValueChar::class.java).value}\'"
            it.getAnnotation(DefaultValueLong::class.java) != null -> it.getAnnotation(DefaultValueLong::class.java).value
            it.getAnnotation(DefaultValueString::class.java) != null -> "\"${it.getAnnotation(DefaultValueString::class.java).value}\""
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

        private fun isElementKeyDefined(it: VariableElement) = it.getAnnotation(Key::class.java) != null

    }

}