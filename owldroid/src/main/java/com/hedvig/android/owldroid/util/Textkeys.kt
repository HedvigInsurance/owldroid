package com.hedvig.android.owldroid.util

fun interpolateTextKey(text: String, replacements: Map<String, String?>): String =
    replacements
        .toList()
        .fold(text) { acc, (key, value) ->
            acc.replace("{$key}", value ?: "")
        }
