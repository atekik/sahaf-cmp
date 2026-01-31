package dev.ktekik.sahaf.cloud

import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.models.BookListing
import dev.ktekik.sahaf.models.CursorPagedListings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant

interface BookApi {
    suspend fun getListingsWithShipping(
        readerId: String,
        excludeSelf: Boolean = true,
        limit: Int? = null,
        before: Instant? = null
    ): Flow<ApiResult<CursorPagedListings>>

    suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>>

    suspend fun createListing(bookListing: BookListing): Flow<ApiResult<BookListing>>
}

fun bookApiBuilder(httpClient: HttpClient, baseUrl: String = "http://192.168.68.67:8080"): BookApi =
    BookApiImpl(httpClient, baseUrl)

private class BookApiImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : BookApi {

    override suspend fun getListingsWithShipping(
        readerId: String,
        excludeSelf: Boolean,
        limit: Int?,
        before: Instant?
    ): Flow<ApiResult<CursorPagedListings>> = flow {
        try {
            val response = httpClient.get("$baseUrl/listings/shipping") {
                parameter("readerId", readerId)
                parameter("excludeSelf", excludeSelf.toString())
                limit?.let { parameter("limit", it) }
                before?.let { parameter("before", it.toString()) }
            }

            if (response.status.isSuccess()) {
                emit(ApiResult.Success(response.body()))
            } else {
                val errorBody = response.bodyAsText()
                val errorMessage =
                    "Request failed with status ${response.status}. Server said: $errorBody"
                println(errorMessage)
                emit(ApiResult.Error(message = errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResult.Error(message = e.message))
        }
    }

    override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> = flow {
        try {
            val response = httpClient.get("$baseUrl/isbn/$isbn")

            if (response.status.isSuccess()) {
                emit(ApiResult.Success(response.body()))
            } else {
                val errorBody = response.bodyAsText()
                val errorMessage =
                    "Request failed with status ${response.status}. Server said: $errorBody"
                println(errorMessage)
                emit(ApiResult.Error(message = errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResult.Error(message = e.message))
        }
    }

    override suspend fun createListing(bookListing: BookListing): Flow<ApiResult<BookListing>> = flow {
        try {
            val response = httpClient.post("$baseUrl/listings") {
                contentType(ContentType.Application.Json)
                setBody(bookListing)
            }

            if (response.status.isSuccess()) {
                emit(ApiResult.Success(response.body()))
            } else {
                val errorBody = response.bodyAsText()
                val errorMessage =
                    "Request failed with status ${response.status}. Server said: $errorBody"
                println(errorMessage)
                emit(ApiResult.Error(message = errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResult.Error(message = e.message))
        }
    }
}
