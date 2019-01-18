package com.hedvig.android.app

import android.content.Context
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun context(application: App): Context {
        return application.baseContext
    }

    @Provides
    @Singleton
    fun simpleCache(context: Context): SimpleCache {
        return SimpleCache(
            File(context.cacheDir, "hedvig_story_video_cache"),
            LeastRecentlyUsedCacheEvictor(10 * 1024 * 1024)
        )
    }

    @Provides
    @Named("GRAPHQL_URL")
    fun provideGraphqlUrl(): String {
        return "https://graphql.dev.hedvigit.com/graphql"
    }
}