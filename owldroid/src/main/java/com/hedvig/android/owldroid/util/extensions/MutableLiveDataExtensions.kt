package com.hedvig.android.owldroid.util.extensions

import android.arch.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.default(initalValue: T): MutableLiveData<T> {
    apply { setValue(initalValue) }
    return this
}
