package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.datastore.ReaderIdZipcodePair
import dev.ktekik.sahaf.datastore.ReaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SaveReaderIdUseCase(
    private val readerRepository: ReaderRepository
) {
    suspend fun execute(pair: ReaderIdZipcodePair) {
        withContext(Dispatchers.IO) {
            readerRepository.savePair(pair)
        }
    }
}
