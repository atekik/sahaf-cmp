package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.home.HomeScreenState
import dev.ktekik.sahaf.home.HomeScreenState.*
import dev.ktekik.sahaf.models.CursorPagedListings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

data class FetchListingsParams(
    val readerId: String,
    val excludeSelf: Boolean = true,
    val limit: Int? = null,
    val before: Instant? = null
)

class FetchListingsWithShippingUseCase(
    private val bookApi: BookApi,
    private val readerApi: ReaderApi
) : UseCase<FetchListingsParams, HomeScreenState> {

    @OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
    override suspend fun
            execute(param: FetchListingsParams): Flow<HomeScreenState> {
        return readerApi.queryReader(param.readerId).combine(
            bookApi.getListingsWithShipping(
                readerId = param.readerId,
                excludeSelf = param.excludeSelf,
                limit = param.limit,
                before = param.before
            )
        ) { readerResult, listingsResult ->
            when (readerResult) {
                is ApiResult.Success -> {
                    when (listingsResult) {
                        is ApiResult.Success<CursorPagedListings> -> ReadyState(
                            readerResult.data,
                            listingsResult.data
                        )

                        is ApiResult.Error -> {
                            ErrorState(listingsResult.message ?: "Generic Error")
                        }
                    }
                }

                is ApiResult.Error -> ErrorState(readerResult.message ?: "Generic Error")
            }

        }
    }
}
