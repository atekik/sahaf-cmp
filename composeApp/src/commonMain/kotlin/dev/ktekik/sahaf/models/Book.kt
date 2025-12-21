package dev.ktekik.sahaf.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val bookId: String?,
    var isbn: String?, // isbn-10
    val infoLink: String,
    val title: String,
    val authors: List<String>,
    val pageCount: Int?,
    val publishedDate: String,
    val categories: List<String>?,
    val cover: ImageLinks?,
    var language: String?,
    var textSnippet: String?,
)

// The top-level response object for Google Books API
@Serializable
data class BookApiResponse(
    val items: List<VolumeItem>
)

// Represents each item in the "items" array
@Serializable
data class VolumeItem(
    val id: String,
    val volumeInfo: VolumeInfo,
    val searchInfo: SearchInfo? = null
)

// Corresponds to the nested "volumeInfo" object
@Serializable
data class VolumeInfo(
    val title: String,
    val authors: List<String> = emptyList(),
    val publishedDate: String,
    val pageCount: Int? = null,
    val categories: List<String> = emptyList(),
    val imageLinks: ImageLinks? = null,
    val language: String,
    val infoLink: String,
    val industryIdentifiers: List<IndustryIdentifier> = emptyList()
)

// For the "imageLinks" object inside "volumeInfo"
@Serializable
data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String,
    val small: String?,
    val medium: String?,
    val large: String?,
    val extraLarge: String?,
)

// For the "industryIdentifiers" array to get the ISBN
@Serializable
data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

// For the "searchInfo" object
@Serializable
data class SearchInfo(
    @SerialName("textSnippet")
    val textSnippet: String? = null
)
