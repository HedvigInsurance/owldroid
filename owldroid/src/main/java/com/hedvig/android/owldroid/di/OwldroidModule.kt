package com.hedvig.android.owldroid.di

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
import javax.inject.Named
import javax.inject.Singleton

@Module
class OwldroidModule {
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
    fun apolloClient(okHttpClient: OkHttpClient, normalizedCacheFactory: NormalizedCacheFactory<LruNormalizedCache>, @Named("GRAPHQL_URL") graphqlUrl: String): ApolloClient {
        return ApolloClient
                .builder()
                .serverUrl(graphqlUrl)
                .okHttpClient(okHttpClient)
                .normalizedCache(normalizedCacheFactory)
                .build()
    }
}