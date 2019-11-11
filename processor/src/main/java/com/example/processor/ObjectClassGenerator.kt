package com.example.processor

import com.squareup.kotlinpoet.*

class ObjectClassGenerator {

    companion object {

        fun generateClasses(): MutableList<FileSpec> {
            val files = mutableListOf<FileSpec>()

            AnnotatedObjectClass.annotatedObjectClass.forEach {
                files.add(generateClass(it))
            }

            return files
        }

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

        private fun generateClass(clazz: AnnotatedObjectClass): FileSpec {
            val classBuilder = TypeSpec.classBuilder("${clazz.getClassName()}Bundler")
            val getBundleFuncBuilder = FunSpec.builder("getBundle")
                .addParameter(
                    ParameterSpec.builder(
                        clazz.getClassName().toLowerCase(),
                        clazz.element.asType().asTypeName()
                    ).build()
                )
                .addStatement("val bundle = %N()", ClassName("android.os", "Bundle").simpleName)

            clazz.fields.forEach {
                getBundleFuncBuilder.addStatement(
                    createBundlePutStatement(
                        clazz.getClassName().toLowerCase(),
                        it
                    )
                )
            }

            getBundleFuncBuilder.returns(ClassName("android.os", "Bundle"))
            getBundleFuncBuilder.addStatement("return bundle")
            classBuilder.addType(TypeSpec.companionObjectBuilder().addFunction(getBundleFuncBuilder.build()).build())

            return FileSpec.builder(clazz.pack, "${clazz.getClassName()}Bundler")
                .addType(classBuilder.build())
                .build()
        }


        private fun createBundlePutStatement(
            modelClassName: String,
            it: Map.Entry<String, ObjectClassField>
        ): String {
            val statement: String

            val typeNameClass = it.value.element.asType().asTypeName()

            if (it.value.isNullable) {
                statement = """
                    if (${modelClassName}.${it.value.element.simpleName} == null) {
                        bundle.put${ModelClassField.BUNDLE_TYPE[typeNameClass.toString()]}("${it.key}", ${it.value.defaultValue})
                    } else {
                        bundle.put${ModelClassField.BUNDLE_TYPE[typeNameClass.toString()]}("${it.key}", ${modelClassName}.${it.value.element.simpleName})
                    }
                """.trimIndent()
            } else {
                statement = """
                    bundle.put${ModelClassField.BUNDLE_TYPE[typeNameClass.toString()]}("${it.key}", ${modelClassName}.${it.value.element.simpleName})
                """.trimIndent()
            }

            return statement
        }
    }
}