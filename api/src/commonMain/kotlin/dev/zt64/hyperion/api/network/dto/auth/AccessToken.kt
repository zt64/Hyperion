package dev.zt64.hyperion.api.network.dto.auth

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(AccessTokenSerializer::class)
sealed interface AccessToken {
    @Serializable
    data class Authorized(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("expires_in")
        val expiresIn: Int,
        val scope: String,
        @SerialName("token_type")
        val tokenType: String
    ) : AccessToken

    sealed interface Error : AccessToken {
        val error: String

        @SerialName("error_description")
        val errorDescription: String

        @Serializable
        data class ExpiredToken(
            override val error: String,
            @SerialName("error_description")
            override val errorDescription: String
        ) : Error

        @Serializable
        data class AuthPending(
            override val error: String,
            @SerialName("error_description")
            override val errorDescription: String
        ) : Error

        @Serializable
        data class Unknown(
            override val error: String,
            @SerialName("error_description")
            override val errorDescription: String
        ) : Error
    }
}

private class AccessTokenSerializer : JsonContentPolymorphicSerializer<AccessToken>(AccessToken::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<AccessToken> {
        return if (element.jsonObject.contains("access_token")) {
            AccessToken.Authorized.serializer()
        } else {
            when (element.jsonObject["error"]?.jsonPrimitive?.content) {
                "expired_token" -> AccessToken.Error.ExpiredToken.serializer()
                "authorization_pending" -> AccessToken.Error.AuthPending.serializer()
                else -> AccessToken.Error.Unknown.serializer()
            }
        }
    }
}