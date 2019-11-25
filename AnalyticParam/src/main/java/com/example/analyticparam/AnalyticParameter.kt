package com.example.analyticparam

class AnalyticParameter {
    val clazz: Any?
    val valueClass: Any?
    val keyClass: Any?
    val required: Map<String, AnalyticParameter>?

    constructor(clazz: Any, required: Map<String, AnalyticParameter>? = null) : this(
        clazz,
        null,
        required
    )

    constructor(
        clazz: Any,
        valueClass: Any?,
        required: Map<String, AnalyticParameter>? = null
    ) : this(clazz, null, valueClass, required)

    constructor(
        clazz: Any,
        keyClass: Any?,
        valueClass: Any?,
        required: Map<String, AnalyticParameter>? = null
    ) {
        this.clazz = clazz
        this.keyClass = keyClass
        this.valueClass = valueClass
        this.required = required
    }
}