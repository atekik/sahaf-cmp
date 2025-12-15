package dev.ktekik.sahaf

import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun HideStatusBarComposable() {

}

@Composable
actual fun ShowStatusBarComposable() {
    TODO("Not yet implemented")
}

actual fun getBaseUrl(): String {
    TODO("Not yet implemented")
}