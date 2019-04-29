package com.hedvig.android.owldroid.util.react

interface AsyncStorageNativeReader {
    fun getKey(key: String): String
    fun deleteKey(key: String)
}
