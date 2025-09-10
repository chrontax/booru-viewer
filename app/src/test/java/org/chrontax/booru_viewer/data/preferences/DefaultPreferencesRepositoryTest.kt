package org.chrontax.booru_viewer.data.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.google.common.truth.Truth.assertThat
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import org.chrontax.booru_viewer.data.preferences.proto.GelbooruSettings
import org.chrontax.booru_viewer.data.preferences.proto.Preferences
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.InputStream
import java.io.OutputStream

object TestPreferencesSerializer : Serializer<Preferences> {
    override val defaultValue: Preferences = Preferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Preferences {
        try {
            return Preferences.parseFrom(input)
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Preferences, output: OutputStream) = t.writeTo(output)
}

class DefaultPreferencesRepositoryTest {

    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var preferencesDataStore: DataStore<Preferences>
    private lateinit var repository: DefaultPreferencesRepository

    @Before
    fun setUp() {
        preferencesDataStore = DataStoreFactory.create(
            serializer = TestPreferencesSerializer,
            scope = CoroutineScope(testScope.coroutineContext),
            produceFile = { temporaryFolder.newFile("test_preferences.pb") })
        repository = DefaultPreferencesRepository(preferencesDataStore)
    }

    private fun generateBooruSite(
        id: String = Uuid.random().toString(),
        name: String = "Test Site $id",
        url: String = "https://test.com/$id",
        type: BooruType = BooruType.DANBOORU
    ): BooruSite {
        val builder = BooruSite.newBuilder().setId(id).setName(name).setUrl(url).setType(type)
        if (type == BooruType.DANBOORU) {
            builder.danbooruSettings =
                DanbooruSettings.newBuilder().setUsername("user").setApiKey("key").build()
        } else if (type == BooruType.GELBOORU) {
            builder.gelbooruSettings =
                GelbooruSettings.newBuilder().setUserId("1234").setApiKey("key").build()
        }
        return builder.build()
    }

    private fun generateTab(
        id: String = Uuid.random().toString(),
        booruId: String,
        name: String = "Test Tab $id",
        tagsList: List<String> = listOf("test_tag")
    ): Tab {
        return Tab.newBuilder().setId(id).setBooruId(booruId).setName(name).addAllTags(tagsList)
            .build()
    }

    @Test
    fun `preferencesFlow emits default preferences initially`() = testScope.runTest {
        val currentPrefs = repository.preferencesFlow.first()
        assertThat(currentPrefs).isEqualTo(Preferences.getDefaultInstance())
    }

    @Test
    fun `addBooruSite successfully adds a new site`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.sitesList).containsExactly(site1)
    }

    @Test
    fun `addBooruSite throws IllegalArgumentException for duplicate ID`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)

        val site2WithSameId = generateBooruSite(id = "site1", url = "https://othersite.com")
        try {
            repository.addBooruSite(site2WithSameId)
            assert(false) { "Should have thrown IllegalArgumentException" }
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("A site with ID 'site1' already exists.")
        }
        assertThat(repository.preferencesFlow.first().sitesList).hasSize(1)
    }

    @Test
    fun `addBooruSite throws IllegalArgumentException for invalid URL`() = testScope.runTest {
        val siteWithInvalidUrl = generateBooruSite(id = "invalid", url = "not_a_url")
        try {
            repository.addBooruSite(siteWithInvalidUrl)
            assert(false) { "Should have thrown IllegalArgumentException for URL" }
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("Invalid URL: not_a_url")
        }
    }

    @Test
    fun `removeBooruSite successfully removes an existing site`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        val site2 = generateBooruSite(id = "site2")
        repository.addBooruSite(site1)
        repository.addBooruSite(site2)

        repository.removeBooruSite("site1")

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.sitesList).containsExactly(site2)
    }

    @Test
    fun `removeBooruSite also removes associated tabs and updates selectedTabId if it was a tab from the removed site`() =
        testScope.runTest {
            val site1 = generateBooruSite(id = "site1")
            val site2 = generateBooruSite(id = "site2")
            repository.addBooruSite(site1)
            repository.addBooruSite(site2)

            val tab1FromSite1 = generateTab(id = "tab1s1", booruId = "site1")
            val tab2FromSite1 = generateTab(id = "tab2s1", booruId = "site1")
            val tab1FromSite2 = generateTab(id = "tab1s2", booruId = "site2")
            repository.addTab(tab1FromSite1)
            repository.addTab(tab2FromSite1)
            repository.addTab(tab1FromSite2)

            // Select a tab that will be removed
            repository.selectTab(tab1FromSite1.id)
            assertThat(repository.preferencesFlow.first().selectedTabId).isEqualTo(tab1FromSite1.id)


            repository.removeBooruSite("site1")

            val prefs = repository.preferencesFlow.first()
            assertThat(prefs.sitesList).containsExactly(site2)
            assertThat(prefs.tabsList).containsExactly(tab1FromSite2)
            assertThat(prefs.selectedTabId).isEqualTo(tab1FromSite2.id)
        }


    @Test
    fun `removeBooruSite throws NoSuchElementException for non-existent ID`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)

        try {
            repository.removeBooruSite("non_existent_site")
            assert(false) { "Should have thrown NoSuchElementException" }
        } catch (e: NoSuchElementException) {
            assertThat(e.message).isEqualTo("No site with ID 'non_existent_site' exists.")
        }
        assertThat(repository.preferencesFlow.first().sitesList).hasSize(1)
    }

    @Test
    fun `updateBooruSite successfully updates an existing site`() = testScope.runTest {
        val originalSite =
            generateBooruSite(id = "site1", url = "https://original.com", name = "Original Name")
        repository.addBooruSite(originalSite)

        val updatedSite =
            BooruSite.newBuilder(originalSite).setUrl("https://updated.com").setName("Updated Name")
                .build()
        repository.updateBooruSite(updatedSite)

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.sitesList).containsExactly(updatedSite)
        assertThat(prefs.sitesList[0].url).isEqualTo("https://updated.com")
        assertThat(prefs.sitesList[0].name).isEqualTo("Updated Name")
    }

    @Test
    fun `updateBooruSite throws NoSuchElementException for non-existent ID`() = testScope.runTest {
        val siteToUpdate = generateBooruSite(id = "non_existent")
        try {
            repository.updateBooruSite(siteToUpdate)
            assert(false) { "Should have thrown NoSuchElementException" }
        } catch (e: NoSuchElementException) {
            assertThat(e.message).isEqualTo("No site with ID '${siteToUpdate.id}' exists.")
        }
    }

    @Test
    fun `updateBooruSite throws IllegalArgumentException for invalid URL`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)
        val siteWithInvalidUrl = BooruSite.newBuilder(site1).setUrl("bad_url").build()
        try {
            repository.updateBooruSite(siteWithInvalidUrl)
            assert(false) { "Should have thrown IllegalArgumentException for URL" }
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("Invalid URL: bad_url")
        }
    }

    @Test
    fun `setPageLimit updates page limit`() = testScope.runTest {
        val newLimit = 50
        repository.setPageLimit(newLimit)
        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.pageLimit).isEqualTo(newLimit)
    }

    @Test
    fun `setPageLimit throws IllegalArgumentException for zero`() = testScope.runTest {
        try {
            repository.setPageLimit(0)
            assert(false) { "Should have thrown IllegalArgumentException for zero limit" }
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("Page limit must be greater than zero.")
        }
        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.pageLimit).isEqualTo(Preferences.getDefaultInstance().pageLimit) // Assuming default is not 0
    }

    @Test
    fun `setPageLimit throws IllegalArgumentException for negative value`() = testScope.runTest {
        try {
            repository.setPageLimit(-5)
            assert(false) { "Should have thrown IllegalArgumentException for negative limit" }
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("Page limit must be greater than zero.")
        }
        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.pageLimit).isEqualTo(Preferences.getDefaultInstance().pageLimit)
    }


    @Test
    fun `setPreviewQuality updates preview quality`() = testScope.runTest {
        val newQuality = PreviewQuality.HIGH
        repository.setPreviewQuality(newQuality)
        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.previewQuality).isEqualTo(newQuality)
    }

    @Test
    fun `setDefaultTags replaces existing tags`() = testScope.runTest {
        preferencesDataStore.updateData {
            it.toBuilder().addAllDefaultTags(listOf("initial1", "initial2")).build()
        }

        val newTags = listOf("newTagA", "newTagB")
        repository.setDefaultTags(newTags)

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.defaultTagsList).containsExactlyElementsIn(newTags).inOrder()
    }

    @Test
    fun `setDefaultTags with empty list clears tags`() = testScope.runTest {
        preferencesDataStore.updateData {
            it.toBuilder().addAllDefaultTags(listOf("initial1")).build()
        }
        repository.setDefaultTags(emptyList())

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.defaultTagsList).isEmpty()
    }

    @Test
    fun `addTab successfully adds a new tab for existing site`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)

        val tab1 = generateTab(
            id = "tab1", booruId = "site1", name = "Tab One", tagsList = listOf("tagA", "tagB")
        )
        repository.addTab(tab1)

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.tabsList).containsExactly(tab1)
        assertThat(prefs.tabsList[0].name).isEqualTo("Tab One")
        assertThat(prefs.tabsList[0].tagsList).containsExactly("tagA", "tagB").inOrder()
        assertThat(prefs.selectedTabId).isEmpty() // Default behavior
    }

    @Test
    fun `addTab throws IllegalArgumentException for duplicate tab ID`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)
        val tab1 = generateTab(id = "tab1", booruId = "site1")
        repository.addTab(tab1)

        val tab2WithSameId =
            generateTab(id = "tab1", booruId = "site1", tagsList = listOf("other_tags"))
        try {
            repository.addTab(tab2WithSameId)
            assert(false) { "Should have thrown IllegalArgumentException" }
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("A tab with ID 'tab1' already exists.")
        }
        assertThat(repository.preferencesFlow.first().tabsList).hasSize(1)
    }

    @Test
    fun `addTab throws NoSuchElementException if booruId does not exist`() = testScope.runTest {
        val tab1 = generateTab(id = "tab1", booruId = "non_existent_site_id")
        try {
            repository.addTab(tab1)
            assert(false) { "Should have thrown NoSuchElementException" }
        } catch (e: NoSuchElementException) {
            assertThat(e.message).isEqualTo("No booru with ID 'non_existent_site_id' exists.")
        }
    }

    @Test
    fun `removeTab successfully removes an existing tab`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)
        val tab1 = generateTab(id = "tab1", booruId = "site1")
        val tab2 = generateTab(id = "tab2", booruId = "site1")
        repository.addTab(tab1)
        repository.addTab(tab2)

        repository.removeTab("tab1")

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.tabsList).containsExactly(tab2)
    }

    @Test
    fun `removeTab sets selectedTabId to a different tab if it was the one removed`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)
        val tab1 = generateTab(id = "tab1", booruId = "site1")
        val tab2 = generateTab(id = "tab2", booruId = "site1")
        repository.addTab(tab1)
        repository.addTab(tab2)
        repository.selectTab(tab1.id)

        assertThat(repository.preferencesFlow.first().selectedTabId).isEqualTo(tab1.id)

        repository.removeTab(tab1.id)

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.tabsList).containsExactly(tab2)
        assertThat(prefs.selectedTabId).isEqualTo(tab2.id)
    }

    @Test
    fun `removeTab creates and selects a new default tab if it the only tab was removed`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)
        val tab1 = generateTab(id = "tab1", booruId = "site1")
        repository.addTab(tab1)
        repository.selectTab(tab1.id)

        assertThat(repository.preferencesFlow.first().selectedTabId).isEqualTo(tab1.id)

        repository.removeTab(tab1.id)

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.tabsList).hasSize(1)
        val newTab = prefs.tabsList[0]
        assertThat(newTab.id).isNotEqualTo(tab1.id)
        assertThat(newTab.booruId).isEqualTo(site1.id)
        assertThat(newTab.name).isEqualTo("Default")
        assertThat(newTab.tagsList).isEqualTo(prefs.defaultTagsList)
        assertThat(prefs.selectedTabId).isEqualTo(newTab.id)
    }


    @Test
    fun `removeTab throws NoSuchElementException for non-existent tab ID`() = testScope.runTest {
        try {
            repository.removeTab("non_existent_tab")
            assert(false) { "Should have thrown NoSuchElementException" }
        } catch (e: NoSuchElementException) {
            assertThat(e.message).isEqualTo("No tab with ID 'non_existent_tab' exists.")
        }
    }

    @Test
    fun `updateTab successfully updates an existing tab`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)
        val originalTab = generateTab(
            id = "tab1", booruId = "site1", name = "Old Name", tagsList = listOf("old_tag")
        )
        repository.addTab(originalTab)

        val updatedTab = Tab.newBuilder(originalTab).setName("New Name").clearTags()
            .addAllTags(listOf("new_tag1", "new_tag2")).build()
        repository.updateTab(updatedTab)

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.tabsList).containsExactly(updatedTab)
        assertThat(prefs.tabsList[0].name).isEqualTo("New Name")
        assertThat(prefs.tabsList[0].tagsList).containsExactly("new_tag1", "new_tag2").inOrder()
    }

    @Test
    fun `updateTab throws NoSuchElementException for non-existent tab ID`() = testScope.runTest {
        val tabToUpdate = generateTab(
            id = "non_existent", booruId = "site1"
        )
        try {
            repository.updateTab(tabToUpdate)
            assert(false) { "Should have thrown NoSuchElementException" }
        } catch (e: NoSuchElementException) {
            assertThat(e.message).isEqualTo("No tab with ID '${tabToUpdate.id}' exists.")
        }
    }

    @Test
    fun `updateTab throws NoSuchElementException if booruId of tab to update does not exist`() =
        testScope.runTest {
            val site1 = generateBooruSite(id = "site1")
            repository.addBooruSite(site1)
            val originalTab = generateTab(id = "tab1", booruId = "site1")
            repository.addTab(originalTab)

            val updatedTabWithInvalidBooruId =
                Tab.newBuilder(originalTab).setBooruId("non_existent_site_for_update").build()
            try {
                repository.updateTab(updatedTabWithInvalidBooruId)
                assert(false) { "Should have thrown NoSuchElementException for booruId" }
            } catch (e: NoSuchElementException) {
                assertThat(e.message).isEqualTo("No booru with ID 'non_existent_site_for_update' exists.")
            }

            val prefs = repository.preferencesFlow.first()
            assertThat(prefs.tabsList).containsExactly(originalTab)
        }


    @Test
    fun `selectTab successfully selects an existing tab`() = testScope.runTest {
        val site1 = generateBooruSite(id = "site1")
        repository.addBooruSite(site1)
        val tab1 = generateTab(id = "tab1", booruId = "site1")
        repository.addTab(tab1)

        repository.selectTab("tab1")

        val prefs = repository.preferencesFlow.first()
        assertThat(prefs.selectedTabId).isEqualTo("tab1")
    }

    @Test
    fun `selectTab throws NoSuchElementException for non-existent tab ID`() = testScope.runTest {
        try {
            repository.selectTab("non_existent_tab")
            assert(false) { "Should have thrown NoSuchElementException" }
        } catch (e: NoSuchElementException) {
            assertThat(e.message).isEqualTo("No tab with ID 'non_existent_tab' exists.")
        }
    }

    @Test
    fun `selectTab with empty string throws NoSuchElementException as empty string is not a valid ID`() =
        testScope.runTest {
            try {
                repository.selectTab("")
                assert(false) { "Should have thrown NoSuchElementException for empty string ID" }
            } catch (e: NoSuchElementException) {
                assertThat(e.message).isEqualTo("No tab with ID '' exists.")
            }
        }
}