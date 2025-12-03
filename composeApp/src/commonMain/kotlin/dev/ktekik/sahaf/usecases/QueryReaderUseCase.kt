package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.home.HomeScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QueryReaderUseCase(
    private val readerApi: ReaderApi,
) : UseCase<String, HomeScreenState> {
    override suspend fun execute(param: String): Flow<HomeScreenState> {
        return readerApi.queryReader(param).map {
            when (it) {
                is ApiResult.Success -> {
                    HomeScreenState.ReadyState(reader = it.data)
                }

                is ApiResult.Error -> {
                    HomeScreenState.ErrorState(error = it.message)
                }
            }
        }
    }
}
