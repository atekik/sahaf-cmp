package dev.ktekik.barcodescanner

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dev.ktekik.barcodescanner.exception.SahafException
import dev.ktekik.barcodescanner.validator.IsbnValidator
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.Executors

sealed interface BarcodeScanningState {
    object Canceled : BarcodeScanningState
    class Scanning(val surfaceRequest: SurfaceRequest? = null) : BarcodeScanningState
    class Scanned(val rawValue: String) : BarcodeScanningState
    class Error(val message: String) : BarcodeScanningState
}

@ExperimentalGetImage
class CameraPreviewViewModel : ViewModel(), ContainerHost<BarcodeScanningState, Unit> {

    override val container: Container<BarcodeScanningState, Unit> = container(
        BarcodeScanningState.Scanning()
    )

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_EAN_13
        ).build()
    )

    val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            intent {
                reduce { BarcodeScanningState.Scanning(newSurfaceRequest) }
            }
        }
    }

    val imageAnalyzerUseCase =
        ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage, imageProxy.imageInfo.rotationDegrees
                        )
                        scanner.process(image).addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                intent {
                                    reduce {
                                        try {
                                            BarcodeScanningState.Scanned(
                                                IsbnValidator.validateISBN(
                                                    barcode.rawValue
                                                )
                                            )
                                        } catch (ex: SahafException) {
                                            BarcodeScanningState.Error(
                                                ex.message ?: "Unknown Error"
                                            )
                                        }
                                    }
                                }
                            }
                        }.addOnCompleteListener {
                            imageProxy.close()
                            mediaImage.close()
                        }.addOnFailureListener { ex ->
                            intent {
                                reduce {
                                    BarcodeScanningState.Error(ex.message ?: "Unknown Error")
                                }
                            }
                        }.addOnCanceledListener {
                            intent {
                                reduce {
                                    BarcodeScanningState.Canceled
                                }
                            }
                        }
                    }
                }
            }

    fun onTryAgainClicked() {
        intent {
            reduce {
                BarcodeScanningState.Scanning()
            }
        }
    }
}
