package org.chrontax.booru_viewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import org.chrontax.booru_viewer.ui.AppInitializer
import org.chrontax.booru_viewer.ui.navigation.AppNavigation
import org.chrontax.booru_viewer.ui.theme.BooruViewerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BooruViewerTheme {
                AppInitializer {
                    AppNavigation()
                }
            }
        }
    }
}