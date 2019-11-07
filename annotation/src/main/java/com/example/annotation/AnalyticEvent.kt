package com.example.annotation

@Target(AnnotationTarget.CLASS)
annotation class AnalyticEvent (
    val nameAsKey: Boolean,
    val eventKey: String
)