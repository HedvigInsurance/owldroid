package com.hedvig.android.owldroid.util

import android.os.Build

fun whenApiVersion(apiVersion: Int, delegate: () -> Unit) {
    if (Build.VERSION.SDK_INT >= apiVersion) {
        delegate()
    }
}