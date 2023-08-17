package dev.zt64.innertube.domain.model

import androidx.compose.runtime.Immutable

/**
 * TODO
 *
 * @property id
 * @property header
 * @property content
 * @property leadingImage
 * @property trailingImage
 * @property sentTimeText
 * @property read
 */
@Immutable
data class Notification(
    val id: Long,
    val header: String,
    val content: String? = null,
    val leadingImage: String,
    val trailingImage: String,
    val sentTimeText: String,
    val read: Boolean
)