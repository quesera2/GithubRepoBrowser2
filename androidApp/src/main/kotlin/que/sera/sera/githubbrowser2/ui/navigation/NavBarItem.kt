package que.sera.sera.githubbrowser2.ui.navigation

import androidx.annotation.DrawableRes
import dev.icerock.moko.resources.desc.StringDesc

data class NavBarItem(
    @param:DrawableRes val icon: Int,
    val description: StringDesc
)