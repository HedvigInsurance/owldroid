package com.hedvig.android.owldroid.data.claims

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.graphql.TriggerClaimChatMutation
import com.hedvig.android.owldroid.type.TriggerClaimChatInput
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClaimsRepository @Inject constructor(private val apolloClient: ApolloClient) {
    private lateinit var claimsQuery: CommonClaimQuery

    fun fetchCommonClaims(): Observable<CommonClaimQuery.Data?> {
        claimsQuery = CommonClaimQuery
            .builder()
            .build()

        return Rx2Apollo
            .from(apolloClient.query(claimsQuery))
            .map { it.data() }
    }

    fun triggerClaimsChat(claimTypeId: String?): Observable<Response<TriggerClaimChatMutation.Data>> {
        val inputBuilder = TriggerClaimChatInput.builder()
        claimTypeId?.let { inputBuilder.claimTypeId(it) }
        val triggerClaimsChatMutation= TriggerClaimChatMutation
            .builder()
            .input(inputBuilder.build())
            .build()

        return Rx2Apollo.from(apolloClient.mutate(triggerClaimsChatMutation))
    }
}

