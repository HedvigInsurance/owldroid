package com.hedvig.android.app

import android.app.Application
import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCache
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
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
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                    Timber.tag("OkHttp").i(it)
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
    }

    @Provides
    @Singleton
    fun lruNormalizedCacheFactory(): NormalizedCacheFactory<LruNormalizedCache> {
        return LruNormalizedCacheFactory(EvictionPolicy.builder().maxSizeBytes(10 * 1024).build())
    }

    @Provides
    @Singleton
    fun apolloClient(okHttpClient: OkHttpClient, normalizedCacheFactory: NormalizedCacheFactory<LruNormalizedCache>): ApolloClient {
        return ApolloClient
                .builder()
                .serverUrl("https://graphql.dev.hedvigit.com/graphql")
                .okHttpClient(okHttpClient)
                .normalizedCache(normalizedCacheFactory)
                .build()
    }
}