package com.example.processor

import com.example.annotation.BundleThis
import com.example.annotation.defaultvalues.DefaultValueProvider
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

open class AnnotatedModelClass(
    val element: TypeElement,
    val pack: String,
    val nameAsKey: Boolean,
    val defaultAll: Boolean,
    val defaultValueProvider: List<DefaultValueProviderFunction>
) {
    companion object {
        val annotatedClasses = mutableSetOf<AnnotatedModelClass>()

        fun getAnnotatedClasses(
            processingEnvironment: ProcessingEnvironment
        ) {
            AnnotationProcessor.bundleableElements.forEach {
                if (it.kind != ElementKind.CLASS) {
                    throw Exception("${BundleThis::class.java.name} can only be applied to a class")
                }

                val pack = processingEnvironment.elementUtils.getPackageOf(it).toString()
                val annotation = it.getAnnotation(BundleThis::class.java)
                val nameAsKey: Boolean = annotation.nameAsKey
                val defaultAll: Boolean = annotation.defaultAll

                val defaultValueFunctions = ElementFilter.methodsIn(it.enclosedElements).filter {
                    it.getAnnotation(DefaultValueProvider::class.java) != null
                }
                val defaultValueProviderFunctions = mutableListOf<DefaultValueProviderFunction>()
                defaultValueFunctions.forEach {
                    val annotation = it.getAnnotation(DefaultValueProvider::class.java)
                    defaultValueProviderFunctions.add(
                        DefaultValueProviderFunction(
                            it,
                            annotation.forKey
                        )
                    )
                }

                annotatedClasses.add(
                    AnnotatedModelClass(
                        it as TypeElement,
                        pack,
                        nameAsKey,
                        defaultAll,
                        defaultValueProviderFunctions
                    )
                )
            }
        }
    }

    val fields: MutableMap<String, ModelClassField> = mutableMapOf()


    fun getClassName() = element.asType().asTypeName().toString().split(".").last()

    fun getFqName() = "${pack}.${getClassName()}"
}