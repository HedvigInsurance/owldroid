package com.hedvig.android.owldroid.data.profile

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.graphql.UpdateEmailMutation
import com.hedvig.android.owldroid.graphql.UpdatePhoneNumberMutation
import io.reactivex.Observable
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apolloClient: ApolloClient) {
    lateinit var profileQuery: ProfileQuery
    fun fetchProfile(): Observable<ProfileQuery.Data?> {
        profileQuery = ProfileQuery
            .builder()
            .build()

        return Rx2Apollo
            .from(apolloClient.query(profileQuery).watcher())
            .map { it.data() }
    }

    fun updateEmail(input: String): Observable<Response<UpdateEmailMutation.Data>> {
        val updateEmailMutation = UpdateEmailMutation
            .builder()
            .input(input)
            .build()

        return Rx2Apollo
            .from(apolloClient.mutate(updateEmailMutation))
    }

    fun updatePhoneNumber(input: String): Observable<Response<UpdatePhoneNumberMutation.Data>> {
        val updatePhoneNumberMutation = UpdatePhoneNumberMutation
            .builder()
            .input(input)
            .build()

        return Rx2Apollo
            .from(apolloClient.mutate(updatePhoneNumberMutation))
    }

    fun writeEmailAndPhoneNumberInCache(email: String?, phoneNumber: String?) {

        val cachedData = apolloClient
            .apolloStore()
            .read(profileQuery)
            .execute()

        val newMemberBuilder = cachedData
            .member()
            .toBuilder()

        email?.let { newMemberBuilder.email(it) }
        phoneNumber?.let { newMemberBuilder.phoneNumber(it) }

        val newData = cachedData
            .toBuilder()
            .member(newMemberBuilder.build())
            .build()

        apolloClient
            .apolloStore()
            .writeAndPublish(profileQuery, newData)
            .execute()
    }
}
