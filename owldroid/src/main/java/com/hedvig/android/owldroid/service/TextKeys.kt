package com.hedvig.android.owldroid.service

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.hedvig.android.owldroid.graphql.TextKeysQuery
import com.ice.restring.Restring
import timber.log.Timber
import javax.inject.Inject

class TextKeys @Inject constructor(val apolloClient: ApolloClient) {
    fun refreshTextKeys() {
        val textKeysQuery = TextKeysQuery
            .builder()
            .build()

        apolloClient
            .query(textKeysQuery)
            .enqueue(object : ApolloCall.Callback<TextKeysQuery.Data>() {
                override fun onStatusEvent(event: ApolloCall.StatusEvent) {
                    Timber.d("StatusEvent: %s", event.toString())
                }

                override fun onFailure(e: ApolloException) {
                    Timber.e(e, "Failed to load text keys")
                }

                override fun onResponse(response: Response<TextKeysQuery.Data>) {
                    val data = response.data()?.languages()

                    data?.forEach { language ->
                        val stringMapping = language.translations()?.map { translation ->
                            translation.key()?.value() to translation.text()
                        }?.toMap()
                        Restring.setStrings(formatLanguageCode(language.code()), stringMapping)
                    }
                }
            })
    }

    fun formatLanguageCode(languageCode: String): String = languageCode.replace("_", "-r")
}
