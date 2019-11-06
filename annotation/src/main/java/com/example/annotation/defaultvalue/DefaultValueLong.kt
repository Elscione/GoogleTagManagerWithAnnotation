package com.example.annotation.defaultvalue

@Target(AnnotationTarget.FIELD)
annotation class DefaultValueLong (val value: Long = 0L)