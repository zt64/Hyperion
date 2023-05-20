package com.zt.innertube.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*


/**
 * A polymorphic serializer that handles InnerTube's singleton map format.
 */
// Credit to https://kotlinlang.slack.com/archives/C7A1U5PTM/p1669319367185639?thread_ts=1669264893.155289&cid=C7A1U5PTM
internal abstract class SingletonMapPolymorphicSerializer<T : Any>(
    tSerializer: KSerializer<T>,
    private val discriminator: String = tSerializer.descriptor.annotations.firstNotNullOfOrNull { it as? JsonClassDiscriminator? }?.discriminator ?: "type"
) : JsonTransformingSerializer<T>(tSerializer) {
    final override fun transformDeserialize(element: JsonElement) = buildJsonObject {
        val (type, value) = element.jsonObject.entries.single()

        put(discriminator, type)

        value.jsonObject.forEach { (k, v) -> put(k, v) }
    }

    final override fun transformSerialize(element: JsonElement) = buildJsonObject {
        putJsonObject(element.jsonObject.getValue(discriminator).jsonPrimitive.content) {
            element.jsonObject.forEach { (k, v) -> if (k != discriminator) put(k, v) }
        }
    }
}