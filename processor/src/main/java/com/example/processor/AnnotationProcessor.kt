package com.example.processor

import com.example.annotation.BundleKey
import com.example.annotation.BundleThis
import com.example.annotation.Default
import com.example.annotation.defaultvalue.*
import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(elements: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {

        if (roundEnv != null) {
            AnnotatedClass.getAnnotatedClasses(roundEnv, processingEnv)
            AnnotatedClass.annotatedClasses.forEach {
                it.fields.putAll(ClassField.getClassFields(it))
            }
            val files = ClassGenerator.generateClasses()

            files.forEach {
                val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
                it.writeTo(File(kaptKotlinGeneratedDir))
            }
        }

        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            BundleThis::class.java.name,
            BundleKey::class.java.name,
            Default::class.java.name,
            DefaultValueString::class.java.name,
            DefaultValueLong::class.java.name,
            DefaultValueChar::class.java.name,
            DefaultValueDouble::class.java.name,
            DefaultValueFloat::class.java.name,
            DefaultValueInt::class.java.name,
            DefaultValueShort::class.java.name,
            DefaultValueBoolean::class.java.name,
            DefaultValueByte::class.java.name
        )
    }
}