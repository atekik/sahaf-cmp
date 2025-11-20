package dev.ktekik.sahaf.reader

import androidx.lifecycle.ViewModel
import dev.ktekik.sahaf.cloud.PostReaderUseCase
import dev.ktekik.sahaf.cloud.Reader
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.Dispatchers
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

class ReaderRegistryViewModel(private val postReaderUseCase: PostReaderUseCase): ContainerHost<ReaderRegistryState, Unit>, ViewModel() {
    override val container: Container<ReaderRegistryState, Unit> = container(ReaderRegistryState())

     fun registerReader(reader: Reader, onSuccess: (reader: Reader) -> Unit, onError: () -> Unit) {
         intent {
             reduce { state.copy(isLoading = true) }
             postReaderUseCase.execute(reader).collect {
                 delay(2000)
                 reduce { state.copy(isLoading = false, reader = it.reader, error = it.error) }

                 withContext(Dispatchers.Main) {
                     it.reader?.let { reader ->
                         onSuccess.invoke(reader)
                     }
                     it.error?.let {
                         onError.invoke()
                     }
                 }
             }
         }
     }
}

@OptIn(ExperimentalUuidApi::class)
fun Profile.toReader(): Reader {
    return Reader(
        name = this.name,
        emailRelay = this.email,
        pictureURL = this.picture,
        activeListings = emptySet(),
        zipcode = "",
        avgRating = 0.0,
        followers = emptySet(),
        following = emptySet(),
        geofenceFiftyKms = emptySet(),
        devices = emptySet(), // Fetch device id
        readerId = null
    )
}