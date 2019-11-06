package com.example.annotation

@Target(AnnotationTarget.FIELD)
annotation class Default (val default: Boolean = false)