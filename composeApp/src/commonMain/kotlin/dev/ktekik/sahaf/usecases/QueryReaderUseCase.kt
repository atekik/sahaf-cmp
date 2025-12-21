package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.models.Reader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QueryReaderUseCase(
    private val readerApi: ReaderApi,
): UseCase<String, ReaderResponse> {
    override suspend fun execute(param: String): Flow<ReaderResponse> {
        return readerApi.queryReader(param).map {
            when (it) {
                is ApiResult.Success -> {
                    ReaderResponse.Success(reader = it.data)
                }

                is ApiResult.Error -> {
                    ReaderResponse.Error(message = it.message)
                }
            }
        }
    }
}

sealed interface ReaderResponse {
    data class Success(val reader: Reader) : ReaderResponse
    data class Error(val message: String?) : ReaderResponse
}
