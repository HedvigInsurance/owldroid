package com.hedvig.android.owldroid.util.react

interface AsyncStorageNative {
    fun getKey(key: String): String
    fun deleteKey(key: String)
}
