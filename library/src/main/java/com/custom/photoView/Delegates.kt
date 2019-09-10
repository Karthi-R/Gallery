package com.custom.photoView

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


internal class InitializationCheck<T>(private val message: String? = null) : ReadWriteProperty<Any?, T> {
    private var value: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException(message ?: "not initialized")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}