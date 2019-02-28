package com.hedvig.android.owldroid.util.apollo

import com.apollographql.apollo.Logger
import com.apollographql.apollo.api.internal.Optional

class NullLogger : Logger {
    override fun log(priority: Int, message: String, t: Optional<Throwable>, vararg args: Any) {
    }
}