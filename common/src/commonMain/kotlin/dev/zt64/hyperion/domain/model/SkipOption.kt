package dev.zt64.hyperion.domain.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.resources.MR

@Stable
enum class SkipOption(val label: StringResource) {
    DISABLE(MR.strings.disable),
    SHOW(MR.strings.show),
    MANUAL(MR.strings.manual_skip),
    AUTO(MR.strings.auto_skip)
}

enum class SponsorBlockCategory(
    val label: StringResource,
    val description: String,
    val defaultColor: Color
) {
    SPONSOR(
        label = MR.strings.sponsor,
        description = "Paid promotion, paid referrals and direct advertisements. Not for " +
            "self-promotion or free shoutouts to causes/creators/websites/products they like.",
        defaultColor = Color(0x7800D400)
    ),
    SELF_PROMO(
        label = MR.strings.self_promo,
        description = "Similar to \"sponsor\" except for unpaid or self promotion. This includes sections " +
            "about merchandise, donations, or information about who they collaborated with.",
        defaultColor = Color(0x78FFFF00)
    ),
    INTERACTION(
        label = MR.strings.interaction,
        description = "Asking for likes, comments, or subscribers. This includes asking for " +
            "engagement in the video, comments, or social media.",
        defaultColor = Color(0x78CC00FF)
    ),
    HIGHLIGHT(
        label = MR.strings.highlight,
        description = "The part of the video that most people are looking for. Similar to " +
            "\"Video starts at x\" comments.",
        defaultColor = Color(0x78FF0000)
    ),
    INTRO(
        label = MR.strings.intro,
        description = "The introduction to the video. This includes the intro animation, intro " +
            "music, and the creator introducing themselves.",
        defaultColor = Color(0x7800FFFF)
    ),
    OUTRO(
        label = MR.strings.outro,
        description = "Credits or when the YouTube endcards appear. Not for conclusions " +
            "with information.",
        defaultColor = Color(0x780202ED)
    ),
    FILLER(
        label = MR.strings.filler,
        description = "Tangential scenes added only for filler or humor that are not required to " +
            "understand the main content of the video.",
        defaultColor = Color(0x787300FF)
    ),
    PREVIEW(
        label = MR.strings.preview,
        description = "Collection of clips that show what is coming up in in this video or other " +
            "videos in a series where all information is repeated later in the video.",
        defaultColor = Color(0x78008FD6)
    ),
    MUSIC_OFF_TOPIC(
        label = MR.strings.non_music,
        description = "Only for use in music videos. This only should be used for sections of music " +
            "videos that aren't already covered by another category.",
        defaultColor = Color(0x78FF9900)
    )
}