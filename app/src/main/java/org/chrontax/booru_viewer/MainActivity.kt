package org.chrontax.booru_viewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.preferences.PreferencesRepository
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.chrontax.booru_viewer.ui.navigation.AppNavigation
import org.chrontax.booru_viewer.ui.theme.BooruViewerTheme
import javax.inject.Inject
import kotlin.uuid.Uuid

private var isAppReady by mutableStateOf(false)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val prefs = preferencesRepository.preferencesFlow.first()
            if (prefs.sitesCount == 0) {
                preferencesRepository.addBooruSite(
                    BooruSite.newBuilder().setUrl("https://danbooru.donmai.us")
                        .setType(BooruType.DANBOORU).setName("Danbooru")
                        .setId(Uuid.random().toString()).setDanbooruSettings(
                            DanbooruSettings.newBuilder().build()
                        ).build()
                )
            }
            if (prefs.pageLimit == 0) {
                preferencesRepository.setPageLimit(20)
            }
            if (prefs.previewQuality == PreviewQuality.UNRECOGNIZED) {
                preferencesRepository.setPreviewQuality(PreviewQuality.LOW)
            }
            if (prefs.defaultTagsCount == 0) {
                preferencesRepository.setDefaultTags(listOf("rating:safe"))
            }
            if (prefs.tabsCount == 0) {
                val prefs = preferencesRepository.preferencesFlow.first()
                val firstBooruId = prefs.sitesList.first().id
                val defaultTags = prefs.defaultTagsList
                val tabId = Uuid.random().toString()

                preferencesRepository.addTab(
                    Tab.newBuilder().setName("Default").setBooruId(firstBooruId)
                        .setId(tabId).addAllTags(defaultTags).build()
                )
                preferencesRepository.selectTab(tabId)
            }

            isAppReady = true
        }

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

@Composable
fun AppInitializer(content: @Composable () -> Unit) {
    if (isAppReady) {
        content()
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}