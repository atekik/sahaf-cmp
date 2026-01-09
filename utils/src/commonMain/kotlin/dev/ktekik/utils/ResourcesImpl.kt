package dev.ktekik.utils

import org.jetbrains.compose.resources.DrawableResource
import sahaf.utils.generated.resources.Res
import sahaf.utils.generated.resources.book_dark_primary
import sahaf.utils.generated.resources.book_light_primary
import sahaf.utils.generated.resources.ic_google
import sahaf.utils.generated.resources.logo
import sahaf.utils.generated.resources.sahafBackground

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
        get() = Res.drawable.logo

    override val bookLight: DrawableResource
        get() = Res.drawable.book_light_primary

    override val bookDark: DrawableResource
        get() = Res.drawable.book_dark_primary
}