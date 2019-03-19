package com.hedvig.android.owldroid.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.Logger
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCache
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.hedvig.android.owldroid.util.react.AsyncStorageNativeReader
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
class OwldroidModule {
    @Provides
    @Singleton
    fun okHttpClient(
        asyncStorageNativeReader: AsyncStorageNativeReader,
        httpLoggingInterceptor: HttpLoggingInterceptor?
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original
                    .newBuilder()
                    .method(original.method(), original.body())
                try {
                    asyncStorageNativeReader.getKey("@hedvig:token")
                } catch (exception: Exception) {
                    null
                }?.let { token ->
                    builder.header("Authorization", token)
                }
                chain.proceed(builder.build())
            }
        httpLoggingInterceptor?.let { builder.addInterceptor(it) }
        return builder.build()
    }

    @Provides
    @Singleton
    fun lruNormalizedCacheFactory(): NormalizedCacheFactory<LruNormalizedCache> {
        return LruNormalizedCacheFactory(EvictionPolicy.builder().maxSizeBytes(10 * 1024).build())
    }

    @Provides
    @Singleton
    fun apolloClient(
        okHttpClient: OkHttpClient,
        normalizedCacheFactory: NormalizedCacheFactory<LruNormalizedCache>,
        @Named("GRAPHQL_URL") graphqlUrl: String,
        logger: Logger?
    ): ApolloClient {
        val builder = ApolloClient
            .builder()
            .serverUrl(graphqlUrl)
            .okHttpClient(okHttpClient)
            .normalizedCache(normalizedCacheFactory)

        logger?.let { builder.logger(it) }

        return builder.build()
    }
}
