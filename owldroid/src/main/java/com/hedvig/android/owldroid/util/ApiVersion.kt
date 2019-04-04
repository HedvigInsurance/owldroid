package com.hedvig.android.owldroid.util

import android.os.Build

inline fun whenApiVersion(apiVersion: Int, delegate: () -> Unit) {
    if (Build.VERSION.SDK_INT >= apiVersion) {
        delegate()
    }
}
