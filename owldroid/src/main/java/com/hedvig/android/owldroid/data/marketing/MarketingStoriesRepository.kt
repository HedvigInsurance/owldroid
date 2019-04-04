package com.hedvig.android.owldroid.data.marketing

import android.content.Context
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.android.owldroid.util.extensions.head
import com.hedvig.android.owldroid.util.extensions.tail
import timber.log.Timber
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

                    data?.tail?.forEach { story ->
                        story.asset()?.let { asset ->
                            cacheAsset(asset).execute()
                        }
                    }

                    data?.head?.asset()?.let { head ->
                        cacheAsset(head) {
                            completion(data.orEmpty())
                        }.execute()
                    }
                }
            })
    }

    private fun cacheAsset(asset: MarketingStoriesQuery.Asset, onEnd: (() -> Unit)? = null) =
        CacheAssetTask(context, cache, asset, onEnd)
}
