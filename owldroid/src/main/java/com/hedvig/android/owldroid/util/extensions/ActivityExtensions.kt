package com.hedvig.android.owldroid.util.extensions

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.util.hasNotch
import com.hedvig.android.owldroid.util.whenApiVersion

fun Activity.setLightNavigationBar() {
    window.navigationBarColor = compatColor(R.color.off_white)
    whenApiVersion(Build.VERSION_CODES.O) {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
}

fun Activity.setDarkNavigationBar() {
    window.navigationBarColor = compatColor(R.color.black)
    whenApiVersion(Build.VERSION_CODES.O) {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
}

fun Activity.showStatusBar() {
    whenApiVersion(Build.VERSION_CODES.M) {
        if (hasNotch()) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = compatColor(R.color.off_white)
            return
        }
    }

    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Activity.hideStatusBar() {
    whenApiVersion(Build.VERSION_CODES.M) {
        if (hasNotch()) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = compatColor(R.color.black)
            return
        }
    }

    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}
