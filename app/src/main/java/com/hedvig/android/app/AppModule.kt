package com.hedvig.android.app

import android.content.Context
import android.support.annotation.Nullable
import com.apollographql.apollo.Logger
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.hedvig.android.owldroid.util.apollo.ApolloTimberLogger
import com.hedvig.android.owldroid.util.react.AsyncStorageNativeReader
import com.hedvig.android.owldroid.util.react.SharedPreferencesAsyncStorageNativeReader
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.File
import javax.inject.Named

@Module
abstract class AppModule {

    @Binds
    abstract fun asyncStorageNativeReader(
        sharedPreferencesAsyncStorageNativeReader: SharedPreferencesAsyncStorageNativeReader
    ): AsyncStorageNativeReader

    @Binds
    @Nullable
    abstract fun provideApolloLogger(apolloTimberLogger: ApolloTimberLogger): Logger?

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun context(application: App) = application.baseContext

        @JvmStatic
        @Provides
        fun simpleCache(context: Context) = SimpleCache(
            File(context.cacheDir, "hedvig_story_video_cache"),
            LeastRecentlyUsedCacheEvictor(10 * 1024 * 1024)
        )

        @JvmStatic
        @Provides
        @Named("GRAPHQL_URL")
        fun provideGraphqlUrl() = "https://graphql.dev.hedvigit.com/graphql"

        @JvmStatic
        @Provides
        @Named("VERSION_NUMBER")
        fun provideVersionNumber() = BuildConfig.VERSION_NAME

        @JvmStatic
        @Provides
        @Named("APPLICATION_ID")
        fun provideApplicationId() = BuildConfig.APPLICATION_ID

        @JvmStatic
        @Provides
        @Nullable
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor? = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { message ->
                Timber.tag("OkHttp").i(message)
            }
        )
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}
