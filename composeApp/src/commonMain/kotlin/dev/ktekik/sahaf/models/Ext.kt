package dev.ktekik.sahaf.models

import dev.ktekik.signin.models.Profile
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Profile.toReader(): Reader {
    return Reader(
        name = this.name,
        emailRelay = this.email,
        pictureURL = this.picture,
        activeListings = emptySet(),
        zipcode = this.zipcode ?: "",
        avgRating = 0.0,
        followers = emptySet(),
        following = emptySet(),
        geofenceFiftyKms = emptySet(),
        devices = emptySet(), // Fetch device id
        readerId = null
    )
}