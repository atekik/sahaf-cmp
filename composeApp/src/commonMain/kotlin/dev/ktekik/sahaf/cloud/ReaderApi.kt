package dev.ktekik.sahaf.cloud

import dev.ktekik.sahaf.reader.ReaderRegistryState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

@OptIn(InternalAPI::class)
class ReaderApiImpl(private val httpClient: HttpClient): ReaderApi {
    override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> =  flow {
        try {
            val httpResponse = httpClient.post("192.168.68.76:8080/readers") {
                contentType(ContentType.Application.Json)
                body = reader
            }

            emit(ApiResult.Success(httpResponse.body()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResult.Error(message = e.message))
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
data class Reader(
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