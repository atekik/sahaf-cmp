package dev.ktekik.sahaf.usecases

import dev.ktekik.sahaf.datastore.ReaderIdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SaveReaderIdUseCase(
    private val readerIdRepository: ReaderIdRepository
) {
    suspend fun execute(readerId: String) {
        withContext(Dispatchers.IO) {
            readerIdRepository.saveId(readerId)
        }
    }
}
