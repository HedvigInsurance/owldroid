package com.hedvig.android.owldroid.util.react

import android.content.Context
import javax.inject.Inject

class SharedPreferencesAsyncStorageNativeReader @Inject constructor(val context: Context) : AsyncStorageNativeReader {

    override fun getKey(key: String): String {
        val sharedPreferences = context.getSharedPreferences("debug", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null) ?: throw Error("Key is not set")
    }
}
