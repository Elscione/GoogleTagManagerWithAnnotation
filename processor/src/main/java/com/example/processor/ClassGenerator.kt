package com.example.processor

import com.example.annotation.BundleThis
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.jvm.jvmStatic

class ClassGenerator {
    companion object {
        fun generateClasses(): MutableList<FileSpec> {
            val files = mutableListOf<FileSpec>()

            AnnotatedClass.annotatedClasses.forEach {
                files.add(generateClass(it))
            }

            return files
        }

        private fun generateClass(clazz: AnnotatedClass): FileSpec {
            val classBuilder = TypeSpec.classBuilder("${clazz.getClassName()}Bundler")
            val getBundleFuncBuilder = FunSpec.builder("getBundle")
                .addParameter(ParameterSpec.builder(clazz.getClassName().toLowerCase(), clazz.element.asType().asTypeName()).build())
                .addStatement("val bundle = %N()", ClassName("android.os", "Bundle").simpleName)

            clazz.fields.forEach {
                getBundleFuncBuilder.addStatement(createBundlePutStatement(clazz.getClassName().toLowerCase(), it))
            }

            getBundleFuncBuilder.returns(ClassName("android.os", "Bundle"))
            getBundleFuncBuilder.addStatement("return bundle")
            classBuilder.addFunction(getBundleFuncBuilder.build())

            return FileSpec.builder(clazz.pack, "${clazz.getClassName()}Bundler")
                .addType(classBuilder.build())
                .build()
        }

        private fun createBundlePutStatement(modelClassName: String, it: Map.Entry<String, ClassField>): String {
            var statement: String

            val typeNameClass = it.value.element.asType().asTypeName()
            val className = typeNameClass.toString().split(".").last()

            if (it.value.isNullable) {
                val default = if (it.value.defaultValue is String) "\"${it.value.defaultValue}\"" else it.value.defaultValue
                statement = """
                    if (${modelClassName}.${it.value.element.simpleName} == null) {
                        bundle.put${className}("${it.key}", $default)
                    } else {
                        bundle.put${className}("${it.key}", ${modelClassName}.${it.value.element.simpleName})
                    }
                """.trimIndent()
            } else {
                statement = """
                    bundle.put${className}("${it.key}", ${modelClassName}.${it.value.element.simpleName})
                """.trimIndent()
            }

            return statement
        }
    }
}