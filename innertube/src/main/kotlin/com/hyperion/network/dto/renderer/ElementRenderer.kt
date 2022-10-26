package com.hyperion.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
internal data class ElementRenderer<T>(private val newElement: NewElement<T>) {
    val model = newElement.type.componentType.model

    @Serializable
    data class NewElement<T>(val type: Type<T>)

    @Serializable
    data class Type<T>(val componentType: ComponentType<T>)

    @Serializable
    data class ComponentType<T>(val model: T)
}