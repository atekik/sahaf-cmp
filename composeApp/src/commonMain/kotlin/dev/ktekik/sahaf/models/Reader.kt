package dev.ktekik.sahaf.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UuidSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uuid {
        return Uuid.parse(decoder.decodeString())
    }
}

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class Reader(
    @Serializable(with = UuidSerializer::class)
    val readerId: Uuid?,
    val name: String,
    val emailRelay: String,
    val pictureURL: String?,
    val activeListings: Set<String>,
    val zipcode: String,
    val avgRating: Double,
    val followers: Set<String>,
    val following: Set<String>,
    var geofenceFiftyKms: Set<String>?,
    val devices: Set<String>,
)
