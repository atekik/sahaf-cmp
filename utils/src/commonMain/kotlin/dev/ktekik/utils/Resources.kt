package dev.ktekik.utils

import org.jetbrains.compose.resources.DrawableResource

interface Resources {
    val drawables: Drawables
}

interface Drawables {
    val googleIcon: DrawableResource

    val background: DrawableResource

    val logo: DrawableResource
}