package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.models.BookListing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface QueryBookListingByIdResult {
    data class Success(val listing: BookListing) : QueryBookListingByIdResult
    data class Error(val message: String) : QueryBookListingByIdResult
}

class QueryBookListingByIdUseCase(
    private val bookApi: BookApi
) : UseCase<String, QueryBookListingByIdResult> {

    override suspend fun execute(param: String): Flow<QueryBookListingByIdResult> {
        return bookApi.queryListingById(param).map { result ->
            when (result) {
                is ApiResult.Success -> QueryBookListingByIdResult.Success(result.data)
                is ApiResult.Error -> QueryBookListingByIdResult.Error(result.message ?: "Unknown error")
            }
        }
    }
}
