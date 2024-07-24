package dev.zt64.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class AccountsList(val contents: Contents) {
    @Serializable
    data class Contents(val accountSectionListRenderer: AccountSectionListRenderer) {
        @Serializable
        data class AccountSectionListRenderer(
            val contents: List<Content>,
            val footers: List<Footer>
        ) {
            @Serializable
            data class Content(val accountItemSectionRenderer: Renderer) {
                @Serializable
                data class Renderer(val contents: List<Content>, val header: Header) {
                    @Serializable
                    data class Content(val accountItem: AccountItem)

                    @Serializable
                    data class Header(val accountItemSectionHeaderRenderer: Renderer) {
                        @Serializable
                        data class Renderer(val title: ApiText)
                    }
                }
            }

            @Serializable
            data class Footer(val accountChannelRenderer: Renderer) {
                @Serializable
                data class Renderer(
                    val navigationEndpoint: ApiNavigationEndpoint,
                    val title: ApiText
                )
            }
        }
    }

    @Serializable
    data class AccountItem(
        val accountByline: ApiText,
        val accountName: ApiText,
        val accountPhoto: ApiImage,
        val hasChannel: Boolean,
        val isDisabled: Boolean,
        val isSelected: Boolean,
        val serviceEndpoint: ServiceEndpoint
    ) {
        @Serializable
        data class ServiceEndpoint(val signInEndpoint: SignInEndpoint) {
            @Serializable
            data class SignInEndpoint(
                val directSigninIdentity: DirectSigninIdentity,
                val directSigninUserProfile: DirectSigninUserProfile,
                val nextEndpoint: ApiNavigationEndpoint
            ) {
                @Serializable
                data class DirectSigninIdentity(
                    val clientIdentityId: String,
                    val datasyncIdToken: DatasyncIdToken,
                    val effectiveObfuscatedGaiaId: String,
                    val gaiaDelegationType: String
                ) {
                    @Serializable
                    data class DatasyncIdToken(val datasyncIdToken: String)
                }

                @Serializable
                data class DirectSigninUserProfile(
                    val accountName: String,
                    val accountPhoto: ApiImage,
                    val email: String
                )
            }
        }
    }
}