package com.example.processor

import com.example.annotation.BundleThis
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class AnnotatedObjectClass(
    val element: TypeElement,
    val pack: String,
    val nameAsKey: Boolean,
    val defaultAll: Boolean,
    val params: MutableMap<String, EventClassField> = mutableMapOf()
) {

    companion object {
        val annotatedObjectClass = mutableSetOf<AnnotatedObjectClass>()

        fun getAnnotatedObjectClass(roundEnvironment: RoundEnvironment, processingEnvironment: ProcessingEnvironment) {
            roundEnvironment.getElementsAnnotatedWith(BundleThis::class.java).forEach {

                if(it.kind != ElementKind.CLASS) {
                    throw Exception("${BundleThis::class.java.name} can only be applied to a class")
                }

                val pack = processingEnvironment.elementUtils.getPackageOf(it).toString()
                val annotation = it.getAnnotation(BundleThis::class.java)
                val nameAsKey: Boolean = annotation.nameAsKey
                val defaultAll: Boolean = annotation.defaultAll

                annotatedObjectClass.add(AnnotatedObjectClass(it as TypeElement, pack, nameAsKey, defaultAll))
            }
        }
    }

    val fields: MutableMap<String, ObjectClassField> = mutableMapOf()

    fun getClassName() = element.asType().asTypeName().toString().split(".").last()

}