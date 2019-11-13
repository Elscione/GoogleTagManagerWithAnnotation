package com.example.processor

import com.example.annotation.*
import com.example.annotation.defaultvalue.*
import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.FilerException
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {

    override fun process(
        elements: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {

        if (roundEnv != null) {
            processAnnotatedModelClass(roundEnv)
            processAnnotatedEventClass(roundEnv)
        }

        return true
    }

    private fun processAnnotatedModelClass(roundEnv: RoundEnvironment) {
        AnnotatedModelClass.getAnnotatedClasses(roundEnv, processingEnv)
        AnnotatedModelClass.annotatedClasses.forEach {
            it.fields.putAll(ModelClassField.getClassFields(it))
        }
        val files = mutableListOf<JavaFile>()
        AnnotatedModelClass.annotatedClasses.forEach {
            files.add(ModelClassGenerator(it).generate())
        }
        try {
            files.forEach {
                it.writeTo(processingEnv.filer)
            }
        } catch (ignored: FilerException) {}
    }

    private fun processAnnotatedEventClass(roundEnv: RoundEnvironment) {
        AnnotatedEventClass.getAnnotatedEventClasses(roundEnv, processingEnv)
        AnnotatedEventClass.annotatedEventClass.forEach {
            it.fields.putAll(ModelClassField.getClassFields(it))
        }
        val files = mutableListOf<JavaFile>()
        AnnotatedEventClass.annotatedEventClass.forEach {
            files.add(EventClassGenerator(it).generate())
        }
        try {
            files.forEach {
                it.writeTo(processingEnv.filer)
            }
        } catch (ignored: FilerException) {}
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            BundleThis::class.java.name,
            Key::class.java.name,
            Default::class.java.name,
            DefaultValueString::class.java.name,
            DefaultValueLong::class.java.name,
            DefaultValueChar::class.java.name,
            DefaultValueDouble::class.java.name,
            DefaultValueFloat::class.java.name,
            DefaultValueInt::class.java.name,
            DefaultValueShort::class.java.name,
            DefaultValueBoolean::class.java.name,
            DefaultValueByte::class.java.name,
            AnalyticEvent::class.java.name,
            EventParam::class.java.name
        )
    }
}