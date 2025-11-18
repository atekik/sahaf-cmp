package dev.ktekik.sahaf.reader

import androidx.lifecycle.ViewModel
import dev.ktekik.sahaf.cloud.PostReaderUseCase
import dev.ktekik.sahaf.cloud.Reader
import dev.ktekik.signin.models.Profile
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.uuid.ExperimentalUuidApi

data class ReaderRegistryState(
    val reader: Reader? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ReaderRegistrySideEffect {
    data object Success: ReaderRegistrySideEffect
    data object Error: ReaderRegistrySideEffect
}

class ReaderRegistryViewModel(private val postReaderUseCase: PostReaderUseCase): ContainerHost<ReaderRegistryState, ReaderRegistrySideEffect>, ViewModel() {
    override val container: Container<ReaderRegistryState, ReaderRegistrySideEffect>
        get() = container<ReaderRegistryState, ReaderRegistrySideEffect>(ReaderRegistryState())

     fun registerReader(reader: Reader) {
         intent {
             reduce { state.copy(isLoading = true) }

             postReaderUseCase.execute(reader).collect {
                 reduce { state.copy(isLoading = false) }

                 it.reader?.let {
                     postSideEffect(ReaderRegistrySideEffect.Success)
                 }
                 it.error?.let {
                     postSideEffect(ReaderRegistrySideEffect.Error)
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