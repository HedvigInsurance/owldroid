package com.hedvig.android.owldroid.data.profile

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.internal.Optional
import com.apollographql.apollo.exception.ApolloException
import com.hedvig.android.owldroid.graphql.ProfileQuery
import timber.log.Timber
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apolloClient: ApolloClient) {
    fun fetchProfile(completion: (result: ProfileQuery.Data?) -> Unit) {
        val profileQuery = ProfileQuery.builder().build()

        apolloClient
                .query(profileQuery)
                .enqueue(object : ApolloCall.Callback<Optional<ProfileQuery.Data>>() {
                    override fun onStatusEvent(event: ApolloCall.StatusEvent) {
                        Timber.d("StatusEvent: %s", event.toString())
                    }

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "Failed to load profile :(")
                    }

                    override fun onResponse(response: Response<Optional<ProfileQuery.Data>>) {
                        completion(response.data()?.get())
                    }
                })
    }
}
