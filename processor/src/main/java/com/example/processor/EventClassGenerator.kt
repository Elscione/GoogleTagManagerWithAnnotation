package com.example.processor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.tools.javac.code.Type
import javax.lang.model.element.VariableElement

class EventClassGenerator {
    companion object {
        val JAVA_TO_KOTLIN_MAP = mutableMapOf<String, TypeName>(
            "java.util.ArrayList" to ClassName("kotlin.collections", "ArrayList"),
            "java.lang.String" to ClassName("kotlin", "String")
        )

        fun generateClasses(): MutableList<FileSpec> {
            val files = mutableListOf<FileSpec>()

            AnnotatedEventClass.annotatedEventClass.forEach {
                files.add(generateClass(it))
            }

            return files
        }

        private fun getKotlinClass(fqName: String, itemType: TypeName): TypeName? {
            val kotlinClass = JAVA_TO_KOTLIN_MAP[fqName]
            if (kotlinClass != null) {
                if (kotlinClass.toString().contains("collections")) {
                    return (kotlinClass as ClassName).parameterizedBy(itemType)
                }
            }

            return kotlinClass
        }

        private fun generateClass(clazz: AnnotatedEventClass): FileSpec {
            val classBuilder = TypeSpec.classBuilder("${clazz.getClassName()}Bundler")
            val getBundleFuncBuilder = FunSpec.builder("getBundle")
                .addStatement("val bundle = %N()", ClassName("android.os", "Bundle").simpleName)

            clazz.params.forEach {
                var fqName = getFqName(it.value.element.asType().asTypeName())
                if (isCollection(fqName)) {
                    val typeName =
                        (it.value.element.asType() as Type.ClassType).typarams_field[0].asTypeName()
                    getBundleFuncBuilder.addParameter(it.key, getKotlinClass(fqName, typeName)!!)
                } else {
                    getBundleFuncBuilder.addParameter(
                        it.key,
                        JAVA_TO_KOTLIN_MAP[it.value.element.asType().asTypeName().toString()]!!
                    )
                }
                getBundleFuncBuilder.addStatement(
                    createPutBundleStatement(
                        it,
                        clazz.getClassName()
                    )
                )
            }

            getBundleFuncBuilder.returns(ClassName("android.os", "Bundle"))
            getBundleFuncBuilder.addStatement("return bundle")
            classBuilder.addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        getBundleFuncBuilder.build()
                    )
                    .addProperty(
                        PropertySpec.builder("KEY", String::class)
                            .initializer("%S", clazz.eventKey)
                            .build()
                    )
                    .build()
            )

            return FileSpec.builder(clazz.pack, "${clazz.getClassName()}Bundler")
                .addType(classBuilder.build())
                .build()
        }

        private fun isCollection(fqName: String): Boolean {
            val kotlinClass = JAVA_TO_KOTLIN_MAP[fqName]
            if (kotlinClass != null) {
                if (kotlinClass.toString().contains("collections")) {
                    return true
                }
            }

            return false
        }

        private fun getFqName(typeName: TypeName): String {
            val typeNameString: String
            if (typeName.toString().contains("<")) {
                typeNameString = typeName.toString().split("<").take(1).joinToString()
            } else {
                typeNameString = typeName.toString()
            }

            return typeNameString
        }

        private fun createPutBundleStatement(
            it: Map.Entry<String, EventClassField>,
            className: String
        ): String {
            if (isPrimitive(it.value.element)) {
                return createPrimitivePutBundleStatement(it)
            } else {
                return createNonPrimitivePutBundleStatement(it)
            }
        }

        private fun createPrimitivePutBundleStatement(
            it: Map.Entry<String, EventClassField>
        ): String {
            val className = it.value.element.asType().asTypeName().toString().split(".").last()
            val statement = """
                bundle.put${className}("${it.key}", ${it.key})
            """.trimIndent()

            return statement
        }

        private fun createNonPrimitivePutBundleStatement(
            it: Map.Entry<String, EventClassField>
        ): String {
            val className = (it.value.element.asType() as Type).tsym.toString()
            val typeName =
                (it.value.element.asType() as Type.ClassType).typarams_field[0].toString()
                    .split(".").last()

            return """
                val ${it.key}_items = ArrayList<Bundle>()
                ${it.key}.forEach {
                    ${it.key}_items.add(${typeName}Bundler.getBundle(it))
                }
                bundle.put${ModelClassField.BUNDLE_TYPE[className]}("${it.key}", ${it.key}_items)
            """.trimIndent()
        }

        private fun isPrimitive(it: VariableElement) =
            ModelClassField.DEFAULT_VALUE[it.asType().asTypeName().toString()] != null
    }
}