package org.chrontax.booru_viewer.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.chrontax.booru_viewer.data.source.BooruSourceFactory
import org.chrontax.booru_viewer.data.source.DefaultBooruSourceFactory
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BooruSourceFactoryModule {

    @Provides
    @Singleton
    fun provideBooruSourceFactory(retrofitBuilder: Retrofit.Builder): BooruSourceFactory =
        DefaultBooruSourceFactory(retrofitBuilder)
}