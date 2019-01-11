package com.hedvig.android.owldroid

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.internal.Optional
import com.apollographql.apollo.exception.ApolloException
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import timber.log.Timber
import javax.inject.Inject

class MarketingStoriesRepository @Inject constructor(private val apolloClient: ApolloClient) {

    fun fetchMarketingStories(completion: (result: List<MarketingStoriesQuery.MarketingStory>) -> Unit) {
        val marketingStoriesQuery = MarketingStoriesQuery.builder()
            .build()

        apolloClient
            .query(marketingStoriesQuery)
            .enqueue(object : ApolloCall.Callback<Optional<MarketingStoriesQuery.Data>>() {

                override fun onStatusEvent(event: ApolloCall.StatusEvent) {
                    Timber.e("StatusEvent: %s", event.toString())
                }

                override fun onFailure(e: ApolloException) {
                    Timber.e("Failed to load marketing stories :(")
                }

                override fun onResponse(response: Response<Optional<MarketingStoriesQuery.Data>>) {
                    val data = response.data()?.get()?.marketingStories()
                    completion(data.orEmpty())
                }
            })
    }
}