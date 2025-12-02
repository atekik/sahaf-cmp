package dev.ktekik.sahaf.reader

import androidx.lifecycle.ViewModel
import dev.ktekik.sahaf.datastore.ReaderIdRepository
import dev.ktekik.sahaf.models.Reader
import dev.ktekik.sahaf.models.toReader
import dev.ktekik.sahaf.usecases.UseCase
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.uuid.ExperimentalUuidApi

data class ReaderRegistryState(
    val reader: Reader? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReaderRegistryViewModel(
    private val postReaderUseCase: UseCase<Reader, ReaderRegistryState>,
    private val readerIdRepository: ReaderIdRepository
) : ContainerHost<ReaderRegistryState, Unit>, ViewModel() {
    override val container: Container<ReaderRegistryState, Unit> = container(ReaderRegistryState())

    @OptIn(ExperimentalUuidApi::class)
    fun registerReader(
        profile: Profile?,
        onSuccess: (reader: Reader) -> Unit,
        onError: () -> Unit = {}
    ) {
        intent {
            reduce { state.copy(isLoading = true) }
            val reader = profile?.toReader() ?: throw IllegalStateException("Profile is null")
            withContext(Dispatchers.IO) {
                postReaderUseCase.execute(reader).collect {
                    // ToDo remove this delay
                    delay(2000)
                    reduce { state.copy(isLoading = false, reader = it.reader, error = it.error) }

                    withContext(Dispatchers.Main) {
                        it.reader?.let { reader ->
                            onSuccess.invoke(reader)

                            // ToDo make this a use case.
                            reader.readerId?.let { readerId ->
                                readerIdRepository.saveId(readerId.toString())
                            }
                        }
                        it.error?.let {
                            onError.invoke()
                        }
                    }
                }
            }
        }
    }
}
