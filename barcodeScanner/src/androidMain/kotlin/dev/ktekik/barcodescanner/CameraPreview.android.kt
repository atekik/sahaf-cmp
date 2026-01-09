package dev.ktekik.barcodescanner

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dev.ktekik.utils.composables.ErrorContainer
import kotlinx.coroutines.awaitCancellation
import org.koin.compose.koinInject

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    onCameraReady: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(modifier)
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "Whoops! Looks like we need your camera to work our magic!" +
                        "Don't worry, we just wanna see your pretty face (and maybe some cats).  " +
                        "Grant us permission and let's get this party started!"
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Hi there! We need your camera to work our magic! ✨\n" +
                        "Grant us permission and let's get this party started! \uD83C\uDF89"
            }
            Text(textToShow, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Give Permission")
            }
        }
    }
}

@Composable
private fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    viewModel: CameraPreviewViewModel = koinInject()
) {
    val barcodeScanningState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    when (val state = barcodeScanningState) {
        BarcodeScanningState.Canceled -> {
            ErrorContainer(
                title = "Something went wrong",
                message = "Barcode scanning was canceled",
                onTryAgainClicked = { viewModel.onTryAgainClicked() }
            )
        }

        is BarcodeScanningState.Error -> {
            ErrorContainer(
                title = "Something went wrong",
                message = state.message,
                onTryAgainClicked = { viewModel.onTryAgainClicked() }
            )
        }

        is BarcodeScanningState.Scanned -> {
            // ToDo send the isbn to server
            Text("Wow success!: ${state.rawValue}")
        }

        is BarcodeScanningState.Scanning -> {
            BarcodeReader(
                modifier,
                state,
                viewModel
            )
        }
    }
}

@Composable
private fun BarcodeReader(
    modifier: Modifier = Modifier,
    state: BarcodeScanningState.Scanning,
    viewModel: CameraPreviewViewModel,
) {
    val appContext = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            DEFAULT_BACK_CAMERA,
            viewModel.cameraPreviewUseCase,
            viewModel.imageAnalyzerUseCase
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }


    state.surfaceRequest?.let {
        CameraXViewfinder(
            surfaceRequest = state.surfaceRequest,
            modifier = modifier
        )

        val overlayBorderColor = MaterialTheme.colorScheme.secondary
        // 2. The Scanning Rectangle Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Define the scanning box size (e.g., 250dp square)
            val boxSize = 240.dp.toPx()

            // Calculate the top-left offset to center it
            val left = (canvasWidth - boxSize) / 2
            val top = (canvasHeight - boxSize) / 2

            // Draw the scanning rectangle (Stroke only)
            drawRect(
                color = overlayBorderColor,
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(boxSize, boxSize),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
            )

            // Optional: Draw a "dimmed" background around the box
            // This makes the scanning area stand out more

            val path = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                addRect(Rect(left, top, left + boxSize, top + boxSize))
                fillType = PathFillType.EvenOdd
            }
            drawPath(path, color = Color.Black.copy(alpha = 0.5f))
        }
    } ?: CircularProgressIndicator()
}