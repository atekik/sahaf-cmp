package dev.ktekik.sahaf.utils

import dev.ktekik.utils.Drawables
import dev.ktekik.utils.Resources
import org.jetbrains.compose.resources.DrawableResource
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.ic_google
import sahaf.composeapp.generated.resources.logo_alt
import sahaf.composeapp.generated.resources.sahafBackground

class ResourcesImpl: Resources {
    override val drawables: Drawables
        get() = DrawablesImpl()

}

private class DrawablesImpl: Drawables {
    override val googleIcon: DrawableResource
        get() = Res.drawable.ic_google

    override val background: DrawableResource
        get() = Res.drawable.sahafBackground

    override val logo: DrawableResource
        get() = Res.drawable.logo_alt
}