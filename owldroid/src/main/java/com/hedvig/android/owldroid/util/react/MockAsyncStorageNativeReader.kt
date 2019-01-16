package com.hedvig.android.owldroid.util.react

class MockAsyncStorageNativeReader: AsyncStorageNativeReader {
    val keys = HashMap<String, String>()
    fun mockKey(key: String, value: String) {
        keys[key] = value
    }

    override fun getKey(key: String): String {
        return keys[key] ?: throw Error("Key not mocked: $key")
    }

}
