package com.hedvig.android.owldroid.util

import android.app.Activity
import android.content.res.Resources
import android.os.Build

@Suppress("unused")
fun hasNotch(activity: Activity): Boolean {
    whenApiVersion(Build.VERSION_CODES.P) {
        val displayCutout = activity.window.decorView.rootWindowInsets.displayCutout
        if (displayCutout != null) {
            return true
        }
    }

    val statusBarHeight = activity.resources.getDimensionPixelSize(
            activity.resources.getIdentifier("status_bar_height", "dimen", "android")
    )
    return (statusBarHeight > convertDpToPixel(24f))
}

fun convertDpToPixel(dp: Float): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return Math.round(px)
}
