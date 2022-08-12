package com.hyperion.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
data class ElementRenderer<T>(val newElement: NewElement<T>) {
    val model = newElement.type.componentType.model

    @Serializable
    data class NewElement<T>(val type: Type<T>)

    @Serializable
    data class Type<T>(val componentType: ComponentType<T>)

    @Serializable
    data class ComponentType<T>(val model: T)
}