package dev.ktekik.sahaf.models

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val bookId: String?,
    var isbn: String?, // isbn-13
    val infoLink: String,
    val title: String,
    val authors: Set<String>,
    val pageCount: Int?,
    val publishedDate: String,
    val publishers: Set<String>?,
    val subjects: Set<String>?,
    val cover: Cover?,
    var snippets: Set<String>?,
)

@Serializable
data class Cover(
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null
)
