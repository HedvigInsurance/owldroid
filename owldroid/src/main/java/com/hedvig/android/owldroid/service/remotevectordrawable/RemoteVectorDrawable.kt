package com.hedvig.android.owldroid.service.remotevectordrawable

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.WorkerThread
import okhttp3.OkHttpClient
import java.net.URL
import javax.inject.Inject

class RemoteVectorDrawable @Inject constructor(private val okHttpClient: OkHttpClient, private val context: Context) {
    @WorkerThread
    fun downloadVectorDrawable(url: URL): Drawable {
/*        val request = Request
            .Builder()
            .get()
            .url(url)
            .build()

        val result = okHttpClient
            .newCall(request)
            .execute()
            .body()?.string() ?: throw Error("Failed to load XML resource")*/

        val result = """
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="32dp"
    android:height="32dp"
    android:viewportWidth="32"
    android:viewportHeight="32">
  <path
      android:pathData="M16,32C24.8366,32 32,24.8366 32,16C32,7.1634 24.8366,0 16,0C7.1634,0 0,7.1634 0,16C0,24.8366 7.1634,32 16,32Z"
      android:fillColor="#FF8A80"
      android:fillType="evenOdd"/>
  <path
      android:pathData="M17.404,6.984H14.572L15.196,19.128H16.804L17.404,6.984ZM14.428,22.56C14.428,23.424 15.124,24.12 15.988,24.12C16.852,24.12 17.572,23.424 17.572,22.56C17.572,21.696 16.852,20.976 15.988,20.976C15.124,20.976 14.428,21.696 14.428,22.56Z"
      android:fillColor="#ffffff"
      android:fillType="evenOdd"/>
</vector>
        """.trimIndent()

        val parsed = VectorDrawableParser.parseVectorDrawable(result)
        val vectorDrawable = VectorDrawableCreator.getVectorDrawable(context, parsed)

        return vectorDrawable ?: throw Error("Failed to compile VectorDrawable")
    }
}
