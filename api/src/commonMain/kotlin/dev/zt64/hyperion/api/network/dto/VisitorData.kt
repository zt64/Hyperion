package dev.zt64.hyperion.api.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class VisitorData(
    @ProtoNumber(1)
    val id: String,
    @ProtoNumber(5)
    val timestamp: Int
)