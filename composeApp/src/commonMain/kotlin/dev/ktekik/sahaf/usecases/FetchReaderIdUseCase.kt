package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.datastore.ReaderIdZipcodePair
import dev.ktekik.sahaf.datastore.ReaderRepository
import kotlinx.coroutines.flow.Flow

class FetchReaderIdUseCase(
    private val readerRepository: ReaderRepository
): UseCase<Unit, String?> {
    override suspend fun execute(param: Unit): Flow<String?> {
        return readerRepository.readerId
    }
}

class FetchReaderIdZipcodePairUseCase(
    private val readerRepository: ReaderRepository
): UseCase<Unit, ReaderIdZipcodePair?> {
    override suspend fun execute(param: Unit): Flow<ReaderIdZipcodePair?> {
        return readerRepository.getPair()
    }
}
