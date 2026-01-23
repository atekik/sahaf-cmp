package dev.ktekik.sahaf.reader

import androidx.lifecycle.ViewModel
import dev.ktekik.sahaf.models.Reader
import dev.ktekik.sahaf.models.toReader
import dev.ktekik.sahaf.usecases.PostReaderUseCase
import dev.ktekik.sahaf.usecases.SaveReaderIdUseCase
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
    private val postReaderUseCase: PostReaderUseCase,
    private val saveReaderIdUseCase: SaveReaderIdUseCase,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
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
                    reduce { state.copy(isLoading = false, reader = it.reader, error = it.error) }

                    withContext(mainDispatcher) {
                        it.reader?.let { reader ->
                            onSuccess.invoke(reader)

                            reader.readerId?.let { readerId ->
                                saveReaderIdUseCase.execute(readerId.toString())
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
