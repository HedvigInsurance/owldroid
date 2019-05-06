package com.hedvig.android.owldroid.util.extensions

import java.io.File
import java.io.InputStream

fun InputStream.into(file: File) {
    use { inputStream ->
        file.outputStream().use { inputStream.copyTo(it) }
    }
}
