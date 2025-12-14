package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.models.CursorPagedListings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

data class FetchListingsParams(
    val readerId: String,
    val excludeSelf: Boolean = true,
    val limit: Int? = null,
    val before: Instant? = null
)

sealed class FetchListingsResult {
    data class Success(val data: CursorPagedListings) : FetchListingsResult()
    data class Error(val message: String?) : FetchListingsResult()
}

class FetchListingsWithShippingUseCase(
    private val bookApi: BookApi,
) : UseCase<FetchListingsParams, FetchListingsResult> {

    override suspend fun execute(param: FetchListingsParams): Flow<FetchListingsResult> {
        return bookApi.getListingsWithShipping(
            readerId = param.readerId,
            excludeSelf = param.excludeSelf,
            limit = param.limit,
            before = param.before
        ).map {
            when (it) {
                is ApiResult.Success -> FetchListingsResult.Success(data = it.data)
                is ApiResult.Error -> FetchListingsResult.Error(message = it.message)
            }
        }
    }
}
