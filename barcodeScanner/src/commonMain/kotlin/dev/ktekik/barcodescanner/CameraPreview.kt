package dev.ktekik.barcodescanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraPreview(
    modifier: Modifier = Modifier,
    onCameraReady: () -> Unit = {}
)