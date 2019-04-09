package com.hedvig.android.owldroid.data.marketing

import android.content.Context
import android.net.Uri
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.android.owldroid.util.extensions.head
import com.hedvig.android.owldroid.util.extensions.tail
import com.squareup.picasso.BuildConfig
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class MarketingStoriesRepository @Inject constructor(
    private val apolloClient: ApolloClient,
    private val context: Context,
    private val cache: SimpleCache
) {

    fun fetchMarketingStories(completion: (result: List<MarketingStoriesQuery.MarketingStory>) -> Unit) {
        val marketingStoriesQuery = MarketingStoriesQuery.builder()
            .build()

        apolloClient
            .query(marketingStoriesQuery)
            .enqueue(object : ApolloCall.Callback<MarketingStoriesQuery.Data>() {

                override fun onStatusEvent(event: ApolloCall.StatusEvent) {
                    Timber.d("StatusEvent: %s", event.toString())
                }

                override fun onFailure(e: ApolloException) {
                    Timber.d("Failed to load marketing stories :(")
                }

                override fun onResponse(response: Response<MarketingStoriesQuery.Data>) {
                    val data = response.data()?.marketingStories()
                    data?.let { cacheAssets(it, completion) } ?: handleNoMarketingStories()
                }
            })
    }

    private fun cacheAssets(data: List<MarketingStoriesQuery.MarketingStory>,
                            completion: (result: List<MarketingStoriesQuery.MarketingStory>) -> Unit) {
        data.tail.forEach { story ->
            story.asset()?.let { GlobalScope.launch {cacheAsset(it)} }
        }

        data.head.asset()?.let { GlobalScope.launch {cacheAsset(it) { completion(data) } } }
    }

    private fun handleNoMarketingStories() = Timber.e("No Marketing Stories")

    private suspend fun cacheAsset(asset: MarketingStoriesQuery.Asset, onEnd: (() -> Unit)? = null) = withContext(Dispatchers.IO) {
        try {
            val mimeType = asset.mimeType()
            val url = asset.url()
            when (mimeType) {
                // TODO Figure out how to make this block the completion of the AsyncTask
                "image/jpeg" -> Picasso.get().load(url).fetch()
                "video/mp4", "video/quicktime" -> {
                    val dataSourceFactory = DefaultDataSourceFactory(
                        context,
                        Util.getUserAgent(
                            context,
                            BuildConfig.APPLICATION_ID
                        )
                    )
                    CacheUtil.cache(
                        DataSpec(Uri.parse(url)),
                        cache,
                        dataSourceFactory.createDataSource(),
                        null
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        //To be sure let's launch this on main thread
        onEnd?.let { GlobalScope.launch(Dispatchers.Main) { it() } }
    }
}
