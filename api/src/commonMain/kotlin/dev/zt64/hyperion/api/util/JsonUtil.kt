package dev.zt64.hyperion.api.util

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

operator fun JsonElement?.get(key: String) = this?.jsonObject?.get(key)