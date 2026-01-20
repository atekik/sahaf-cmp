package dev.ktekik.sahaf.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class BookListing(
    @Serializable(with = UuidSerializer::class)
    val readerId: Uuid,
    val book: Book,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.LocalPickup,
    val likes: Set<String>,
    val viewCount: Int = 0,
    @Serializable(with = UuidSerializer::class)
    val listingUuid: Uuid?,
    val readersContacted: Set<String>,
    val zipcode: String,
)

@Serializable
enum class DeliveryMethod {
    LocalPickup,
    Shipping,
    LocalPickupAndShipping,
}

@Serializable
data class CursorPagedListings(
    val items: List<BookListing> = emptyList(),
    val nextCursor: Instant?,
)
