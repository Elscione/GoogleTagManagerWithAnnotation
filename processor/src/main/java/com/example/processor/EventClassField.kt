package com.example.processor

import com.example.annotation.Key
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement

class EventClassField(
    val element: VariableElement
) {
    companion object {
        fun getClassFields(clazz: AnnotatedEventClass): Map<String, EventClassField> {
            val fields = mutableMapOf<String, EventClassField>()
            val elements = clazz.element.enclosedElements

            elements.forEach {
                if (it.kind == ElementKind.FIELD && clazz.nameAsKey) {
                    val key = getKey(it as VariableElement, clazz.nameAsKey)

                    if (key != null) {
                        fields[key] = EventClassField(it)
                    }
                }
            }

            return fields
        }

        private fun getKey(it: VariableElement, nameAsKey: Boolean): String? {
            if (nameAsKey) {
                return it.simpleName.toString()
            } else {
                if (it.getAnnotation(Key::class.java) != null) {
                    return it.getAnnotation(Key::class.java).key
                } else {
                    return null
                }
            }
        }
    }
}