package com.example.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AnalyticEvent(
    val nameAsKey: Boolean,
    val eventKey: String,
    val rulesClass: KClass<*>
)