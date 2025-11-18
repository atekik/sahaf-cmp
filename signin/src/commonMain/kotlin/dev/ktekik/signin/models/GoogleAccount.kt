package dev.ktekik.signin.models

import kotlinx.serialization.Serializable

data class GoogleAccount(
    val idToken: String,
    val accessToken: String,
    val profile: Profile
)

@Serializable
data class Profile(
    val name: String,
    val familyName: String,
    val givenName: String,
    val email: String,
    val picture: String?,
    val zipcode: String? = null,
)