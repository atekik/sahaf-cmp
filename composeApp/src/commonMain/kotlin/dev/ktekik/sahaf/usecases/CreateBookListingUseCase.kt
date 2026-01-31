package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.models.BookListing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface CreateBookListingResult {
    data class Success(val listing: BookListing) : CreateBookListingResult
    data class Error(val message: String) : CreateBookListingResult
}

class CreateBookListingUseCase(
    private val bookApi: BookApi
) : UseCase<BookListing, CreateBookListingResult> {

    override suspend fun execute(param: BookListing): Flow<CreateBookListingResult> {
        return bookApi.createListing(param).map { result ->
            when (result) {
                is ApiResult.Success -> CreateBookListingResult.Success(result.data)
                is ApiResult.Error -> CreateBookListingResult.Error(result.message ?: "Unknown error")
            }
        }
    }
}
