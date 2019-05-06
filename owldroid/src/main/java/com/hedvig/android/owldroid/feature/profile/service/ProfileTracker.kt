package com.hedvig.android.owldroid.feature.profile.service

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class ProfileTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun clickReferral(incentive: Int?) = firebaseAnalytics.logEvent(
        "click_referral",
        Bundle().apply {
            incentive?.let { putInt("incentive", it) }
        }
    )

    fun howDoesItWorkClick() = firebaseAnalytics.logEvent("click_charity_how_does_it_work", null)
}
