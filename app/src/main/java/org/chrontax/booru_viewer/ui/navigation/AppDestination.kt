package org.chrontax.booru_viewer.ui.navigation

import androidx.navigation.NamedNavArgument

sealed interface AppDestination {
    val route: String

    object Home : AppDestination {
        override val route: String = "home"
    }

    object Settings : AppDestination {
        override val route: String = "settings"
    }
}