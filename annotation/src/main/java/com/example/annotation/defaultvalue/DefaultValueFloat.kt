package com.example.annotation.defaultvalue

@Target(AnnotationTarget.FIELD)
annotation class DefaultValueFloat (val value: Float = 0.0f)