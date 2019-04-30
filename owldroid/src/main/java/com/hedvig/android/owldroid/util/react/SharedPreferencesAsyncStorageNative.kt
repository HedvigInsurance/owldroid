package com.hedvig.android.owldroid.util.react

import android.content.Context
import timber.log.Timber
import javax.inject.Inject

class SharedPreferencesAsyncStorageNative @Inject constructor(val context: Context) : AsyncStorageNative {
    override fun setKey(key: String, value: String) {
        Timber.e("not used!?")
    }

    override fun getKey(key: String): String {
        val sharedPreferences = context.getSharedPreferences("debug", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null) ?: throw Exception("Key is not set")
    }

    override fun deleteKey(key: String) {
        Timber.i("not used!?")
    }
}
