package com.hedvig.android.owldroid.data.profile

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.BankAccountQuery
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.graphql.SelectCashbackMutation
import com.hedvig.android.owldroid.graphql.StartDirectDebitRegistrationMutation
import com.hedvig.android.owldroid.graphql.UpdateEmailMutation
import com.hedvig.android.owldroid.graphql.UpdatePhoneNumberMutation
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    fun selectCashback(id: String) {
        val selectCashbackMutation = SelectCashbackMutation
                .builder()
                .id(id)
                .build()

        apolloClient
                .mutate(selectCashbackMutation)
                .enqueue(object : ApolloCall.Callback<SelectCashbackMutation.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "Failed to select cashback")
                    }

                    override fun onResponse(response: Response<SelectCashbackMutation.Data>) {
                        response.data()?.selectCashbackOption()?.let { selectedCashback ->
                            val cachedData = apolloClient
                                    .apolloStore()
                                    .read(profileQuery)
                                    .execute()

                            val newCashback = ProfileQuery.Cashback
                                    .builder()
                                    .__typename(selectedCashback.__typename())
                                    .name(selectedCashback.name())
                                    .imageUrl(selectedCashback.imageUrl())
                                    .paragraph(selectedCashback.paragraph())
                                    .build()

                            val newData = ProfileQuery.Data(
                                    cachedData.member(),
                                    cachedData.insurance(),
                                    cachedData.bankAccount(),
                                    newCashback,
                                    cachedData.cashbackOptions()
                            )

                            apolloClient
                                    .apolloStore()
                                    .writeAndPublish(profileQuery, newData)
                                    .execute()
                        }
                    }
                })
    }

    fun startTrustlySession(): Observable<StartDirectDebitRegistrationMutation.Data> {
        val startDirectDebitRegistrationMutation = StartDirectDebitRegistrationMutation
                .builder()
                .build()

        return Rx2Apollo
                .from(apolloClient.mutate(startDirectDebitRegistrationMutation))
                .map { it.data() }
    }

    fun refreshBankAccountInfo() {
        apolloClient
                .apolloStore()
                .remove(CacheKey.from("bankAccount"))
                .execute()

        val bankAccountQuery = BankAccountQuery
                .builder()
                .build()

        apolloClient
                .query(bankAccountQuery)
                .enqueue(object : ApolloCall.Callback<BankAccountQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Timber.e(e, "Failed to fetch bank account information")
                    }

                    override fun onResponse(response: Response<BankAccountQuery.Data>) {
                        response.data()?.bankAccount()?.let { bankAccount ->
                            val cachedData = apolloClient
                                    .apolloStore()
                                    .read(profileQuery)
                                    .execute()

                            val newBankAccount = ProfileQuery.BankAccount
                                    .builder()
                                    .__typename(bankAccount.__typename())
                                    .bankName(bankAccount.bankName())
                                    .descriptor(bankAccount.descriptor())
                                    .build()

                            val newData = ProfileQuery.Data(
                                    cachedData.member(),
                                    cachedData.insurance(),
                                    newBankAccount,
                                    cachedData.cashback(),
                                    cachedData.cashbackOptions()
                            )

                            apolloClient
                                    .apolloStore()
                                    .writeAndPublish(profileQuery, newData)
                                    .execute()
                        }
                    }
                })
    }
}
