package com.hedvig.android.owldroid.data.claims

import com.apollographql.apollo.ApolloClient
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClaimsRepository @Inject constructor(private val apolloClient: ApolloClient) {

    //Todo: don't mock!
    fun fetchQuickActions(): Observable<List<ClaimsQuickAction>>
        = Observable.just(listOf(ClaimsQuickAction("icon", "title", "subtitle")))
}

data class ClaimsQuickAction(val icon: String, val title: String, val subtitle: String)
