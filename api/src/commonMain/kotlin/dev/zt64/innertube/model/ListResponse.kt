package dev.zt64.innertube.model

data class ListResponse<T>(
    val results: List<T>,
    val nextToken: String?,
    val prevToken: String?
)