package dev.zt64.hyperion.api.network.dto.browse

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class ApiLibrary(override val contents: Contents<JsonElement>) : ApiBrowse()