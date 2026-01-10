package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.models.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface IsbnQueryResult {
    data class Success(val book: Book) : IsbnQueryResult
    data class Error(val message: String) : IsbnQueryResult
}

class IsbnQueryUseCase(
    private val bookApi: BookApi
) : UseCase<String, IsbnQueryResult> {

    override suspend fun execute(param: String): Flow<IsbnQueryResult> {
        return bookApi.queryByIsbn(param).map { result ->
            when (result) {
                is ApiResult.Success -> IsbnQueryResult.Success(result.data)
                is ApiResult.Error -> IsbnQueryResult.Error(result.message ?: "Unknown error")
            }
        }
    }
}
