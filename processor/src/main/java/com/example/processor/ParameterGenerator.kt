package com.example.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement

class ParameterGenerator {
    companion object {
        fun createParameter(varElement: VariableElement): ParameterSpec {
            return ParameterSpec.get(varElement)
        }

        fun createParameter(
            name: String,
            parameterType: ClassName,
            vararg modifiers: Modifier
        ): ParameterSpec {
            return ParameterSpec.builder(parameterType, name, *modifiers).build()
        }

        fun createParameter(
            name: String,
            parameterType: TypeName,
            vararg modifiers: Modifier
        ): ParameterSpec {
            return ParameterSpec.builder(parameterType, name, *modifiers).build()
        }

        fun createParameterizedParameter(
            name: String,
            parameterType: ClassName,
            vararg param: TypeName
        ): ParameterSpec {
            return ParameterSpec.builder(ParameterizedTypeName.get(parameterType, *param), name)
                .build()
        }
    }
}