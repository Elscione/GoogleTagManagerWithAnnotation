package com.example.processor

import com.example.analyticparam.AnalyticParameter
import com.example.processor.ClassGenerator.Companion.BUNDLE_NAME
import com.example.processor.ClassGenerator.Companion.bundleClassName
import com.example.processor.ClassGenerator.Companion.listClassName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

class RulesCheckerGenerator {
    companion object {
        val requiredMapName = "required"
        val classBuilder = TypeSpec.classBuilder("AnalyticRequirementChecker")
            .addModifiers(Modifier.PUBLIC)
        val requirementCheckerFuncBuilder = MethodSpec.methodBuilder("checkRequired")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterGenerator.createParameterizedParameter(
                    requiredMapName,
                    ClassName.get(Map::class.java),
                    ClassName.get(String::class.java),
                    ClassName.get(AnalyticParameter::class.java)
                )
            )
            .addParameter(
                ParameterGenerator.createParameter(
                    "bundle",
                    ClassName.get("android.os", "Bundle")
                )
            )
            .addException(Exception::class.java)

        fun generate(): JavaFile {
            generateCheckerMethod()

            return JavaFile.builder(
                "com.analytic.paramchecker",
                classBuilder.addMethod(
                    requirementCheckerFuncBuilder.build()
                ).build()
            ).indent("    ").build()
        }

        private fun generateCheckerMethod() {
            requirementCheckerFuncBuilder
                .beginControlFlow(
                    "if (!\$N.keySet().containsAll(\$N.keySet())) ",
                    BUNDLE_NAME,
                    requiredMapName
                )
                .addStatement(
                    "throw new \$T(\$S)",
                    ClassName.get("java.lang", "Exception"),
                    "Some required field is missing!"
                )
                .endControlFlow()

            requirementCheckerFuncBuilder
                .beginControlFlow(
                    "for (\$T \$N : \$N.keySet()) ",
                    ClassName.get(String::class.java),
                    "key",
                    requiredMapName
                )
                .beginControlFlow(
                    "if (\$N.get(\$N) instanceof \$T) ",
                    BUNDLE_NAME,
                    "key",
                    bundleClassName
                )
                .addStatement(
                    "\$N(((\$T) \$N.get(\$N)).getRequired(), (\$T) \$N.get(\$N) )",
                    "checkRequired",
                    ClassName.get(AnalyticParameter::class.java),
                    requiredMapName,
                    "key",
                    bundleClassName,
                    BUNDLE_NAME,
                    "key"
                )
                .nextControlFlow(
                    "else if (\$N.get(\$N) instanceof \$T) ",
                    BUNDLE_NAME,
                    "key",
                    listClassName
                )
                .addStatement(
                    "\$N(((\$T) \$N.get(\$N)).getRequired(), (\$T) ((\$T) \$N.get(\$N)).get(0))",
                    "checkRequired",
                    ClassName.get(AnalyticParameter::class.java),
                    requiredMapName,
                    "key",
                    bundleClassName,
                    listClassName,
                    BUNDLE_NAME,
                    "key"
                )
                .endControlFlow()
                .endControlFlow()
        }
    }
}