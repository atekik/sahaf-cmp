package dev.ktekik.barcodescanner

import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CameraPreviewViewModelTest {

    @Test
    fun `initial state should be Scanning with null surfaceRequest`() = runTest {
        val viewModel = CameraPreviewViewModel()

        viewModel.test(this) {
            val state = awaitState()
            assertTrue(state is BarcodeScanningState.Scanning)
            assertNull((state as BarcodeScanningState.Scanning).surfaceRequest)
        }
    }

    @Test
    fun `onTryAgainClicked should reset state to Scanning`() = runTest {
        val viewModel = CameraPreviewViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            containerHost.onTryAgainClicked()
            
            val state = awaitState()
            assertTrue(state is BarcodeScanningState.Scanning)
            assertNull((state as BarcodeScanningState.Scanning).surfaceRequest)
        }
    }

    @Test
    fun `Scanning state surfaceRequest should be null initially`() = runTest {
        val viewModel = CameraPreviewViewModel()

        viewModel.test(this) {
            val state = awaitState() as BarcodeScanningState.Scanning
            assertNull(state.surfaceRequest)
        }
    }

    @Test
    fun `multiple onTryAgainClicked calls should all reset to Scanning state`() = runTest {
        val viewModel = CameraPreviewViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            // First reset
            containerHost.onTryAgainClicked()
            var state = awaitState()
            assertTrue(state is BarcodeScanningState.Scanning)
            
            // Second reset
            containerHost.onTryAgainClicked()
            state = awaitState()
            assertTrue(state is BarcodeScanningState.Scanning)
            
            // Third reset
            containerHost.onTryAgainClicked()
            state = awaitState()
            assertTrue(state is BarcodeScanningState.Scanning)
        }
    }
}
