package com.hedvig.android.owldroid.service

import android.content.Context
import com.hedvig.android.owldroid.service.remotevectordrawable.RemoteVectorDrawable
import okhttp3.OkHttpClient
import okhttp3.mock.ClasspathResources.resource
import okhttp3.mock.MediaTypes.MEDIATYPE_XML
import okhttp3.mock.MockInterceptor
import okhttp3.mock.eq
import okhttp3.mock.get
import okhttp3.mock.rule
import okhttp3.mock.url
import org.junit.Test
import java.net.URL

class RemoteVectorDrawableTest {
    val mockClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(MockInterceptor().apply {
                rule(get, url eq MOCK_URL) {
                    respond(resource("test_vector_drawable.xml"), MEDIATYPE_XML)
                }
            })
            .build()
    }

    @Test
    fun shouldReturnVectorDrawable() {
        // val sut = RemoteVectorDrawable(mockClient)

        // val vectorDrawable = sut.downloadVectorDrawable(URL(MOCK_URL))
    }

    companion object {
        const val MOCK_URL = "https://www.example.com/"
    }
}
