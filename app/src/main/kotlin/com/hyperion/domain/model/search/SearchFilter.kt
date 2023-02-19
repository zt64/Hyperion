package com.hyperion.domain.model.search

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.hyperion.R

sealed interface SearchFilter {
    @get:StringRes
    val displayName: Int

    @Immutable
    enum class UploadDate(@StringRes override val displayName: Int) : SearchFilter {
        LAST_HOUR(R.string.last_hour),
        TODAY(R.string.today),
        THIS_WEEK(R.string.this_week),
        THIS_MONTH(R.string.this_month),
        THIS_YEAR(R.string.this_year)
    }

    @Immutable
    enum class Type(@StringRes override val displayName: Int) : SearchFilter {
        VIDEO(R.string.video),
        CHANNEL(R.string.channel),
        PLAYLIST(R.string.playlist),
        MOVIE(R.string.movie)
    }

    @Immutable
    enum class Duration(@StringRes override val displayName: Int) : SearchFilter {
        SHORT(R.string.short_video),
        MEDIUM(R.string.medium_video),
        LONG(R.string.long_video)
    }

    @Immutable
    enum class Feature(@StringRes override val displayName: Int) : SearchFilter {
        LIVE(R.string.live),
        UHD(R.string.uhd),
        HD(R.string.hd),
        SUBTITLES(R.string.subtitles),
        CREATIVE_COMMONS(R.string.creative_commons),
        SPHERICAL(R.string.spherical),
        VR(R.string.vr),
        SPATIAL(R.string.spatial),
        HDR(R.string.hdr),
        LOCATION(R.string.location)
    }

    @Immutable
    enum class Sort(@StringRes override val displayName: Int) : SearchFilter {
        RELEVANCE(R.string.relevance),
        UPLOAD_DATE(R.string.upload_date),
        VIEW_COUNT(R.string.view_count),
        RATING(R.string.rating)
    }
}

