package com.example.annotation.defaultvalues

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultValueProvider(val forKey: String)