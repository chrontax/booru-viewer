package org.chrontax.booru_viewer.data.source

import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.source.danbooru.DanbooruSource
import org.chrontax.booru_viewer.data.source.gelbooru.GelbooruSource
import retrofit2.Retrofit
import javax.inject.Inject

class DefaultBooruSourceFactory @Inject constructor(val retrofitBuilder: Retrofit.Builder) :
    BooruSourceFactory {

    override fun create(booruSite: BooruSite): BooruSource =
        when (booruSite.type) {
            BooruType.DANBOORU -> DanbooruSource(
                retrofitBuilder,
                booruSite.url,
                booruSite.danbooruSettings
            )

            BooruType.GELBOORU -> GelbooruSource(
                retrofitBuilder,
                booruSite.url,
                booruSite.gelbooruSettings
            )
            BooruType.UNRECOGNIZED -> error("Unrecognized Booru type")
        }
}