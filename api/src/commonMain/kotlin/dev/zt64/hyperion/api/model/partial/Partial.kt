package dev.zt64.hyperion.api.model.partial

interface Partial

interface PartialVideo : Partial {
    val title: String
    val description: String?
    val publishedAt: String
}