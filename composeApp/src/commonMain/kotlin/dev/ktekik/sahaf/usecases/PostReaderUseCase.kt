package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.models.Reader
import dev.ktekik.sahaf.reader.ReaderRegistryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostReaderUseCase(private val readerApi: ReaderApi): UseCase<Reader, ReaderRegistryState> {
    override suspend fun execute(param: Reader): Flow<ReaderRegistryState> {
        return readerApi.postReader(param).map {
            when(it) {
                is ApiResult.Success -> ReaderRegistryState(reader = it.data)
                is ApiResult.Error -> ReaderRegistryState(error = it.message)
            }
        }
    }
}
