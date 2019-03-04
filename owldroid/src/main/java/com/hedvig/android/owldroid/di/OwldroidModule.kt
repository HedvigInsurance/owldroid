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
            httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor { interceptorChain ->
                    val original = interceptorChain.request()
                    val builder = original.newBuilder().method(original.method(), original.body())
                    var token: String? = null
                    try {
                        token = asyncStorageNativeReader.getKey("@hedvig:token")
                    } catch (exception: Exception) {

                    }
                    token?.let { builder.header("Authorization", it) }
                    interceptorChain.proceed(builder.build())
                }
                .addInterceptor(httpLoggingInterceptor)
                .build()
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
            logger: Logger
    ): ApolloClient {
        return ApolloClient
                .builder()
                .serverUrl(graphqlUrl)
                .okHttpClient(okHttpClient)
                .normalizedCache(normalizedCacheFactory)
                .logger(logger)
                .build()
    }

}