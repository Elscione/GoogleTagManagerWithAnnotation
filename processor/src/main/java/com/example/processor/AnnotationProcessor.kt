package com.example.processor

import com.example.annotation.AnalyticEvent
import com.example.annotation.BundleThis
import com.example.annotation.Default
import com.example.annotation.Key
import com.example.annotation.defaultvalue.*
import com.example.processor.utils.isList
import com.example.processor.utils.isMap
import com.example.processor.utils.isSet
import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import com.sun.tools.javac.code.Type
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.FilerException
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class AnnotationProcessor : AbstractProcessor() {

    companion object {
        val foundParams = mutableSetOf<String>()
    }

    override fun process(
        elements: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {

        if (roundEnv != null) {
            processAnnotatedModelClass(roundEnv)
            processAnnotatedEventClass(roundEnv)

            generateFiles()
        }

        return true
    }

    private fun processAnnotatedModelClass(roundEnv: RoundEnvironment) {
        AnnotatedModelClass.getAnnotatedClasses(roundEnv, processingEnv)
        AnnotatedModelClass.annotatedClasses.forEach {
            it.fields.putAll(ModelClassField.getClassFields(it))
        }
    }

    private fun processAnnotatedEventClass(roundEnv: RoundEnvironment) {
        AnnotatedEventClass.getAnnotatedEventClasses(roundEnv, processingEnv)
        AnnotatedEventClass.annotatedEventClass.forEach {
            it.fields.putAll(ModelClassField.getClassFields(it))
        }

        AnnotatedEventClass.annotatedEventClass.forEach {
            validateRequired(it.element)
        }
    }

    private fun generateFiles() {
        val modelFiles = mutableListOf<JavaFile>()
        AnnotatedModelClass.annotatedClasses.forEach {
            modelFiles.add(ModelClassGenerator(it).generate())
        }
        try {
            modelFiles.forEach {
                it.writeTo(processingEnv.filer)
            }
        } catch (ignored: FilerException) {
        }

        val eventFiles = mutableListOf<JavaFile>()
        AnnotatedEventClass.annotatedEventClass.forEach {
            eventFiles.add(EventClassGenerator(it).generate())
        }
        try {
            eventFiles.forEach {
                it.writeTo(processingEnv.filer)
            }
        } catch (ignored: FilerException) {
        }
    }

    private fun validateRequired(classElement: TypeElement, vararg outerClass: TypeMirror) {
        val type = classElement.asType() as Type.ClassType

        val outerClassNames = StringBuilder("")
        outerClass.forEach {
            outerClassNames.append("${it.toString().split(".").last()}Model\$")
        }
        val modelName = "${type.toString().split(".").last()}Model"
        val modelClass =
            Class.forName("com.example.processor.models.${outerClassNames}${modelName}").kotlin
        val required: Map<*, *> =
            modelClass.companionObject?.memberProperties?.find {
                it.name == modelName.replace("Model", "Bundle").decapitalize()
            }?.getter?.call(
                modelClass.companionObjectInstance
            ) as Map<*, *>

        val matchRequired = mutableSetOf<String>()

        ModelClassField.keys[type.toString()]?.forEach {
            if (required.containsKey(it.key)) {
                if (required[it.key] != it.value.second) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Element with key ${it.key} must be ${required[it.key]}",
                        it.value.first
                    )
                }
                matchRequired.add(it.key)
            }
        }


        if (matchRequired.size < required.size) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Some required bundle element is not present", classElement
            )
        }

        val fields = ElementFilter.fieldsIn(classElement.enclosedElements)

        fields.forEach {
            validateRequired(it.asType(), *outerClass, classElement.asType())
        }
    }

    private fun validateRequired(elementType: TypeMirror, vararg outerClass: TypeMirror) {
        val fieldTypeName = TypeName.get(elementType)

        if (isList(fieldTypeName) || isSet(fieldTypeName)) {
            validateRequired(
                (elementType as Type.ClassType).typarams_field[0].asElement() as TypeElement,
                *outerClass
            )
        } else if (isMap(fieldTypeName)) {
            validateRequired(
                (elementType as Type.ClassType).typarams_field[1].asElement() as TypeElement,
                *outerClass
            )
        }
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
            AnalyticEvent::class.java.name
        )
    }


}