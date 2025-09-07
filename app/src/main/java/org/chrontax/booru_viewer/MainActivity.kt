package org.chrontax.booru_viewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.preferences.PreferencesRepository
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import org.chrontax.booru_viewer.ui.navigation.AppNavigation
import org.chrontax.booru_viewer.ui.screens.home.HomeScreen
import org.chrontax.booru_viewer.ui.theme.BooruViewerTheme
import javax.inject.Inject
import kotlin.uuid.Uuid

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            if (preferencesRepository.preferencesFlow.first().sitesCount == 0) {
                preferencesRepository.addBooruSite(
                    BooruSite.newBuilder().setUrl("https://danbooru.donmai.us")
                        .setType(BooruType.DANBOORU).setName("Danbooru")
                        .setId(Uuid.random().toString()).setDanbooruSettings(
                            DanbooruSettings.newBuilder().build()
                        ).build()
                )
            }
        }

        enableEdgeToEdge()
        setContent {
            BooruViewerTheme {
                AppNavigation()
            }
        }
    }
}