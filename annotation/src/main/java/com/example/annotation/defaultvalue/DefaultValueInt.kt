package com.example.annotation.defaultvalue

@Target(AnnotationTarget.FIELD)
annotation class DefaultValueInt (val value: Int = 0)