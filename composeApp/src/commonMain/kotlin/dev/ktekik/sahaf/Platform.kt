package dev.ktekik.sahaf

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getBaseUrl(): String

@Composable
expect fun HideStatusBarComposable()

@Composable
expect fun ShowStatusBarComposable()