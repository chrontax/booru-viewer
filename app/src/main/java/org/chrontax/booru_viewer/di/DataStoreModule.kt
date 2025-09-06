package org.chrontax.booru_viewer.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.chrontax.booru_viewer.data.preferences.DefaultPreferencesRepository
import org.chrontax.booru_viewer.data.preferences.PreferencesRepository
import org.chrontax.booru_viewer.data.preferences.preferencesStore
import org.chrontax.booru_viewer.data.preferences.proto.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.preferencesStore

    @Singleton
    @Provides
    fun providePreferencesRepository(
        preferencesDataStore: DataStore<Preferences>
    ): PreferencesRepository = DefaultPreferencesRepository(preferencesDataStore)
}