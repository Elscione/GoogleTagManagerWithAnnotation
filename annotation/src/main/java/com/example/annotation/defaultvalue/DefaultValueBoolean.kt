package com.example.annotation.defaultvalue

@Target(AnnotationTarget.FIELD)
annotation class DefaultValueBoolean (val value: Boolean = false)