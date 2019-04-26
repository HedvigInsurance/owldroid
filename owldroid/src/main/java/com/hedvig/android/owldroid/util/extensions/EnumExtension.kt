package com.hedvig.android.owldroid.util.extensions

inline fun <reified E: Enum<E>> byOrdinal(index: Int) = enumValues<E>()[index]
