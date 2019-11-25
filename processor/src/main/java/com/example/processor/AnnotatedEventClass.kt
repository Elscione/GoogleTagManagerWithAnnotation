package com.example.processor

import com.example.annotation.AnalyticEvent
import com.example.annotation.defaultvalues.DefaultValueProvider
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter

class AnnotatedEventClass(
    element: TypeElement,
    pack: String,
    nameAsKey: Boolean,
    val eventKey: String,
    val rulesClass: TypeMirror,
    defaultValueProvider: List<DefaultValueProviderFunction>
) : AnnotatedModelClass(element, pack, nameAsKey, true, defaultValueProvider) {
    companion object {
        val annotatedEventClass = mutableSetOf<AnnotatedEventClass>()

        fun getAnnotatedEventClasses(
            processingEnv: ProcessingEnvironment
        ) {
            AnnotationProcessor.analyticEventElements.forEach {
                if (it.kind != ElementKind.CLASS) {
                    throw Exception("${AnalyticEvent::class.java.name} can only be applied to a class")
                }

                val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                val annotation = it.getAnnotation(AnalyticEvent::class.java)
                val nameAsKey = annotation.nameAsKey
                val eventKey = annotation.eventKey
                var rulesClass: TypeMirror? = null

                val defaultValueFunctions = ElementFilter.methodsIn(it.enclosedElements).filter {
                    (it.getAnnotation(DefaultValueProvider::class.java) != null)
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

                try {
                    annotation.rulesClass
                } catch (e: MirroredTypeException) {
                    rulesClass = e.typeMirror
                }

                annotatedEventClass.add(
                    AnnotatedEventClass(
                        it as TypeElement,
                        pack,
                        nameAsKey,
                        eventKey,
                        rulesClass!!,
                        defaultValueProviderFunctions
                    )
                )
            }
        }
    }
}