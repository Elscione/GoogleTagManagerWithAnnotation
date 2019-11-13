package com.example.processor

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier

// This class is used to generate the model bundler classes
class ModelClassGenerator(clazz: AnnotatedModelClass) : ClassGenerator(clazz) {
    override val getBundleFuncBuilder: MethodSpec.Builder = MethodSpec
        .methodBuilder("getBundle")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(
            ParameterGenerator
                .createParameter(
                    clazz.getClassName().decapitalize(),
                    TypeName.get(clazz.element.asType())
                )
        )
        .addStatement(
            "\$T $BUNDLE_NAME = new \$T()",
            bundleClassName,
            bundleClassName
        )

    override fun generate(): JavaFile {
        clazz.fields.forEach {
            getBundleFuncBuilder.addCode(createPutStatement(it.value))
        }

        return JavaFile.builder(
            clazz.pack,
            classBuilder.addMethod(
                getBundleFuncBuilder
                    .addStatement("return bundle")
                    .returns(bundleClassName)
                    .build())
                .build())
            .indent("    ")
            .build()
    }
}