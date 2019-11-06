package com.example.annotation

@Target(AnnotationTarget.CLASS)
annotation class BundleThis(val nameAsKey: Boolean = false, val defaultAll: Boolean = false)