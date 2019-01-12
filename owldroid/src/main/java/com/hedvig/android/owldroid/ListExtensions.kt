package com.hedvig.android.owldroid

val<T> List<T>.tail: List<T>
    get() = subList(1, size)

val<T> List<T>.head: T
    get() = first()