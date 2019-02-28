package com.hedvig.android.owldroid.util.extensions

import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager

val Fragment.localBroadcastManager get() = LocalBroadcastManager.getInstance(context!!)