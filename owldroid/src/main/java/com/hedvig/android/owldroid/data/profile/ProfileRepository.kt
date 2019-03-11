package com.hedvig.android.owldroid.data.profile

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.graphql.UpdateEmailMutation
import com.hedvig.android.owldroid.graphql.UpdatePhoneNumberMutation
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apolloClient: ApolloClient) {
    lateinit var profileQuery: ProfileQuery
    fun fetchProfile(): Observable<ProfileQuery.Data?> {
        profileQuery = ProfileQuery
            .builder()
            .build()

        return Rx2Apollo.from(apolloClient.query(profileQuery).watcher())
            .map { it.data() }
    }

    fun updateEmail(input: String) {
        val updateEmailMutation = UpdateEmailMutation
            .builder()
            .input(input)
            .build()

        apolloClient
            .mutate(updateEmailMutation)
            .enqueue(object : ApolloCall.Callback<UpdateEmailMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    Timber.e(e, "Failed to update email")
                }

                override fun onResponse(response: Response<UpdateEmailMutation.Data>) {
                    val email = response.data()?.updateEmail()?.email()

                    val cachedData = apolloClient
                        .apolloStore()
                        .read(profileQuery)
                        .execute()

                    val oldMember = cachedData.member()
                    val newMember = ProfileQuery.Member
                        .builder()
                        .__typename(oldMember.__typename())
                        .id(oldMember.id())
                        .firstName(oldMember.firstName())
                        .lastName(oldMember.lastName())
                        .phoneNumber(oldMember.phoneNumber())
                        .email(email)
                        .build()
                    val newData = ProfileQuery.Data(
                        newMember,
                        cachedData.insurance(),
                        cachedData.bankAccount(),
                        cachedData.cashback()
                    )

                    apolloClient
                        .apolloStore()
                        .writeAndPublish(profileQuery, newData)
                }
            })
    }

    fun updatePhoneNumber(input: String) {
        val updatePhoneNumberMutation = UpdatePhoneNumberMutation
            .builder()
            .input(input)
            .build()

        apolloClient
            .mutate(updatePhoneNumberMutation)
            .enqueue(object : ApolloCall.Callback<UpdatePhoneNumberMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    Timber.e(e, "Failed to update phone number")
                }

                override fun onResponse(response: Response<UpdatePhoneNumberMutation.Data>) {
                    val phoneNumber = response.data()?.updatePhoneNumber()?.phoneNumber()

                    val cachedData = apolloClient
                        .apolloStore()
                        .read(profileQuery)
                        .execute()

                    val oldMember = cachedData.member()
                    val newMember = ProfileQuery.Member
                        .builder()
                        .__typename(oldMember.__typename())
                        .id(oldMember.id())
                        .firstName(oldMember.firstName())
                        .lastName(oldMember.lastName())
                        .email(oldMember.email())
                        .phoneNumber(phoneNumber)
                        .build()

                    val newData = ProfileQuery.Data(
                        newMember,
                        cachedData.insurance(),
                        cachedData.bankAccount(),
                        cachedData.cashback()
                    )

                    apolloClient
                        .apolloStore()
                        .writeAndPublish(profileQuery, newData)
                }
            })
    }
}
