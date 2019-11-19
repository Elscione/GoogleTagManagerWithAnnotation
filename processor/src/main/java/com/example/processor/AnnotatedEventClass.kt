package com.example.processor

import com.example.annotation.AnalyticEvent
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class AnnotatedEventClass(
    element: TypeElement,
    pack: String,
    nameAsKey: Boolean,
    val eventKey: String
) : AnnotatedModelClass(element, pack, nameAsKey, true) {
    companion object {
        val annotatedEventClass = mutableSetOf<AnnotatedEventClass>()

        fun getAnnotatedEventClasses(
            roundEnv: RoundEnvironment,
            processingEnv: ProcessingEnvironment
        ) {
            roundEnv.getElementsAnnotatedWith(AnalyticEvent::class.java).forEach {
                if (it.kind != ElementKind.CLASS) {
                    throw Exception("${AnalyticEvent::class.java.name} can only be applied to a class")
                }

                val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                val annotation = it.getAnnotation(AnalyticEvent::class.java)
                val nameAsKey = annotation.nameAsKey
                val eventKey = annotation.eventKey

                annotatedEventClass.add(
                    AnnotatedEventClass(
                        it as TypeElement,
                        pack,
                        nameAsKey,
                        eventKey
                    )
                )
            }
        }
    }
}