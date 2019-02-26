package com.hedvig.android.owldroid.data.profile

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ProfileQuery
import io.reactivex.Observable
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apolloClient: ApolloClient) {
    fun fetchProfile(): Observable<ProfileQuery.Data?> {
        val profileQuery = ProfileQuery.builder().build()

        return Rx2Apollo.from(apolloClient.query(profileQuery))
            .map { it.data()?.orNull() }
    }
}
