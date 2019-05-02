package com.hedvig.android.owldroid.feature.dashboard.service

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class DashboardTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun perilClick(perilId: String?) = firebaseAnalytics.logEvent(
        "peril_click",
        Bundle().apply {
            putString("peril_id", perilId)
        }
    )

    fun setupDirectDebit() = firebaseAnalytics.logEvent("dashboard_setup_direct_debit", null)
    fun expandInsurancePendingInfo() = firebaseAnalytics.logEvent("expand_insurance_pending_info", null)
    fun collapseInsurancePendingInfo() = firebaseAnalytics.logEvent("collapse_insurance_pending_info", null)
}
