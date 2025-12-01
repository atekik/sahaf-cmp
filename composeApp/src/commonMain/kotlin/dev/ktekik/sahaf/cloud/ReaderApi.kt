package dev.ktekik.sahaf.cloud

import dev.ktekik.sahaf.reader.ReaderRegistryState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PostReaderUseCase(private val readerApi: ReaderApi) {
    suspend fun execute(reader: Reader): Flow<ReaderRegistryState> {
        return readerApi.postReader(reader).map {
            when(it) {
                is ApiResult.Success -> ReaderRegistryState(reader = it.data)
                is ApiResult.Error -> ReaderRegistryState(error = it.message)
            }
        }
    }
}

interface ReaderApi {
    suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>>
}

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String?) : ApiResult<Nothing> // Using Nothing for error case
}

class ReaderApiImpl(private val httpClient: HttpClient): ReaderApi {
    override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> =  flow {
        try {
            val httpClient = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }

            val response = httpClient.post("http://10.0.2.2:8080/readers") {
                contentType(ContentType.Application.Json)
                setBody(reader)
            }

            if (response.status.isSuccess()) {
                emit(ApiResult.Success(response.body()))
            } else {
                val errorBody = response.bodyAsText()
                val errorMessage = "Request failed with status ${response.status}. Server said: $errorBody"
                println(errorMessage)
                emit(ApiResult.Error(message = errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResult.Error(message = e.message))
        }
    }
}

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
