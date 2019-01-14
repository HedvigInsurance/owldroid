package com.hedvig.android.owldroid.util

val<T> List<T>.tail: List<T>
    get() = subList(1, size)

val<T> List<T>.head: T
    get() = first()