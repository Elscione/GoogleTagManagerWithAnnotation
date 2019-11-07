package com.example.processor

import com.example.annotation.BundleThis
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class AnnotatedModelClass {
    companion object {
        val annotatedClasses = mutableSetOf<AnnotatedModelClass>()

        fun getAnnotatedClasses(roundEnv: RoundEnvironment, processingEnvironment: ProcessingEnvironment) {
            roundEnv.getElementsAnnotatedWith(BundleThis::class.java).forEach {
                if (it.kind != ElementKind.CLASS) {
                    throw Exception("${BundleThis::class.java.name} can only be applied to a class")
                }

                val pack = processingEnvironment.elementUtils.getPackageOf(it).toString()
                val annotation = it.getAnnotation(BundleThis::class.java)
                val nameAsKey: Boolean = annotation.nameAsKey
                val defaultAll: Boolean = annotation.defaultAll

                annotatedClasses.add(AnnotatedModelClass(it as TypeElement, pack, nameAsKey, defaultAll))
            }
        }
    }

    val element: TypeElement
    val pack: String
    val nameAsKey: Boolean
    val defaultAll: Boolean
    val fields: MutableMap<String, ModelClassField> = mutableMapOf()

    constructor(element: TypeElement, pack: String, nameAsKey: Boolean, defaultAll: Boolean) {
        this.element = element
        this.pack = pack
        this.nameAsKey = nameAsKey
        this.defaultAll = defaultAll
    }

    fun getClassName() = element.asType().asTypeName().toString().split(".").last()
}