package dev.zt64.hyperion.domain.model.search

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.StringResource
import dev.zt64.hyperion.resources.MR

@Immutable
sealed interface SearchFilter {
    val displayName: StringResource

    enum class UploadDate(override val displayName: StringResource) : SearchFilter {
        LAST_HOUR(MR.strings.last_hour),
        TODAY(MR.strings.today),
        THIS_WEEK(MR.strings.this_week),
        THIS_MONTH(MR.strings.this_month),
        THIS_YEAR(MR.strings.this_year)
    }

    enum class Type(override val displayName: StringResource) : SearchFilter {
        VIDEO(MR.strings.video),
        CHANNEL(MR.strings.channel),
        PLAYLIST(MR.strings.playlist),
        MOVIE(MR.strings.movie)
    }

    enum class Duration(override val displayName: StringResource) : SearchFilter {
        SHORT(MR.strings.short_video),
        MEDIUM(MR.strings.medium_video),
        LONG(MR.strings.long_video)
    }

    enum class Feature(override val displayName: StringResource) : SearchFilter {
        LIVE(MR.strings.live),
        UHD(MR.strings.uhd),
        HD(MR.strings.hd),
        SUBTITLES(MR.strings.subtitles),
        CREATIVE_COMMONS(MR.strings.creative_commons),
        SPHERICAL(MR.strings.spherical),
        VR(MR.strings.vr),
        SPATIAL(MR.strings.spatial),
        HDR(MR.strings.hdr),
        LOCATION(MR.strings.location)
    }

    enum class Sort(override val displayName: StringResource) : SearchFilter {
        RELEVANCE(MR.strings.relevance),
        UPLOAD_DATE(MR.strings.upload_date),
        VIEW_COUNT(MR.strings.view_count),
        RATING(MR.strings.rating)
    }
}