package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface DeleteBookListingResult {
    data object Success : DeleteBookListingResult
    data class Error(val message: String) : DeleteBookListingResult
}

class DeleteBookListingUseCase(
    private val bookApi: BookApi
) : UseCase<String, DeleteBookListingResult> {

    override suspend fun execute(param: String): Flow<DeleteBookListingResult> {
        return bookApi.deleteListing(param).map { result ->
            when (result) {
                is ApiResult.Success -> DeleteBookListingResult.Success
                is ApiResult.Error -> DeleteBookListingResult.Error(result.message ?: "Unknown error")
            }
        }
    }
}
