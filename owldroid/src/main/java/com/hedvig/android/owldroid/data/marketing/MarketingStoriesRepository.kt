package com.hedvig.android.owldroid.data.marketing

import android.content.Context
import android.net.Uri
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.internal.Optional
import com.apollographql.apollo.exception.ApolloException
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.hedvig.android.owldroid.BuildConfig
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.android.owldroid.util.head
import com.hedvig.android.owldroid.util.tail
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
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
            .enqueue(object : ApolloCall.Callback<Optional<MarketingStoriesQuery.Data>>() {

                override fun onStatusEvent(event: ApolloCall.StatusEvent) {
                    Timber.d("StatusEvent: %s", event.toString())
                }

                override fun onFailure(e: ApolloException) {
                    Timber.d("Failed to load marketing stories :(")
                }

                override fun onResponse(response: Response<Optional<MarketingStoriesQuery.Data>>) {
                    val data = response.data()?.get()?.marketingStories()

                    data?.tail?.forEach {
                        cacheAsset(it).run()
                    }

                    val head = data?.head
                    cacheAsset(head!!).run {
                        completion(data.orEmpty())
                    }
                }
            })
    }

    private fun cacheAsset(story: MarketingStoriesQuery.MarketingStory): FutureTask<Unit> {
        return FutureTask(Callable {
            val asset = story.asset().get()
            val mimeType = asset.mimeType().get()
            val url = asset.url()
            if (mimeType == "image/jpeg") {
                // TODO Figure out how to make this block the completion of the FutureTask
                Picasso.get().load(url).fetch()
            } else if (mimeType == "video/mp4" || mimeType == "video/quicktime") {
                val dataSourceFactory =
                    DefaultDataSourceFactory(
                        context, Util.getUserAgent(
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
        })
    }
}