package dev.ktekik.sahaf.cloud

import dev.ktekik.sahaf.models.Reader
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ReaderApi {
    suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>>
}

fun readerApiBuilder(httpClient: HttpClient): ReaderApi = ReaderApiImpl(httpClient)

private class ReaderApiImpl(private val httpClient: HttpClient): ReaderApi {
    override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> =  flow {
        try {
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
