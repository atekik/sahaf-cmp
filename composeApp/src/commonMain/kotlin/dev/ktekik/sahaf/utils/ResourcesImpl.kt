package dev.ktekik.sahaf.utils

import dev.ktekik.utils.Drawables
import dev.ktekik.utils.Resources
import org.jetbrains.compose.resources.DrawableResource
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.ic_google

class ResourcesImpl: Resources {
    override val drawables: Drawables
        get() = DrawablesImpl()

}

private class DrawablesImpl: Drawables {
    override val googleIcon: DrawableResource
        get() = Res.drawable.ic_google
}