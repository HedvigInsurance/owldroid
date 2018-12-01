package com.hedvig.android.app

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun lruNormalizedCacheFactory(): LruNormalizedCacheFactory {
        return LruNormalizedCacheFactory(EvictionPolicy.builder().maxSizeBytes(10 * 1024).build())
    }

    @Provides
    @Singleton
    fun client(okHttpClient: OkHttpClient, lruNormalizedCacheFactory: LruNormalizedCacheFactory): ApolloClient {
        return ApolloClient
                .builder()
                .serverUrl("https://graphql.dev.hedvigit.com/graphql")
                .okHttpClient(okHttpClient)
                .normalizedCache(lruNormalizedCacheFactory)
                .build()
    }
}