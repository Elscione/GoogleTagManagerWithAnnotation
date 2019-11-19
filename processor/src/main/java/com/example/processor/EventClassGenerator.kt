package com.example.processor

import com.squareup.javapoet.*
import javax.lang.model.element.Modifier
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

// This class is used to generate the event bundler classes
class EventClassGenerator(clazz: AnnotatedEventClass) : ClassGenerator(clazz) {

    override val getBundleFuncBuilder: MethodSpec.Builder = MethodSpec
        .methodBuilder("getBundle")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addStatement(
            "\$T $BUNDLE_NAME = new \$T()",
            bundleClassName,
            bundleClassName
        )

    override val getBundleFromMap: MethodSpec.Builder = MethodSpec
        .methodBuilder("getBundle")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(
            ParameterGenerator
                .createParameterizedParameter(
                    ClassName.get(Map::class.java),
                    TypeName.get(String::class.java),
                    ClassName.get("java.lang", "Object")
                )
        )
        .addStatement(
            "\$T $BUNDLE_NAME = new \$T()",
            bundleClassName,
            bundleClassName
        )

    // this is where we start generating the class
    override fun generate(): JavaFile {
        generateEventKeyProperty()
        clazz.fields.forEach {
            getBundleFuncBuilder.addParameter(
                ParameterGenerator.createParameter(
                    it.value.element.simpleName.toString(),
                    TypeName.get(it.value.element.asType())
                )
            )
            getBundleFuncBuilder.addCode(createPutStatement(it.value))
            getBundleFromMap.addCode(createPutStatementFromMap(it.value))
        }

        return JavaFile.builder(
            clazz.pack,
            classBuilder.addMethod(
                getBundleFuncBuilder
                    .addStatement("return bundle")
                    .returns(bundleClassName)
                    .build()
            )
                .addMethod(
                    getBundleFromMap
                        .addStatement("return bundle")
                        .returns(bundleClassName)
                        .build()
                )
                .build()
        )
            .indent("    ")
            .build()
    }

    // this function is used to generate the event key property
    // we get it's value from @ValueEvent property named eventKey
    private fun generateEventKeyProperty() {
        classBuilder.addField(
            FieldSpec.builder(
                ClassName.get("java.lang", "String"),
                "KEY",
                Modifier.PUBLIC,
                Modifier.STATIC,
                Modifier.FINAL
            )
                .initializer("\$S", (clazz as AnnotatedEventClass).eventKey)
                .build()
        )
    }
}