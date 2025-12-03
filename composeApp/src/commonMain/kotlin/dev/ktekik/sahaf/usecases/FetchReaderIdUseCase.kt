package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.datastore.ReaderIdRepository
import kotlinx.coroutines.flow.Flow

class FetchReaderIdUseCase(
    private val readerIdRepository: ReaderIdRepository
): UseCase<Unit, String?> {
    override suspend fun execute(param: Unit): Flow<String?> {
        return readerIdRepository.readerId
    }
}
