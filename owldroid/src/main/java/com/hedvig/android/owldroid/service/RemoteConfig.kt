package com.hedvig.android.owldroid.service

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject
import javax.inject.Singleton

const val DEFAULT_INCENTIVE = 100L

@Singleton
class RemoteConfig @Inject constructor() {
    val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        firebaseRemoteConfig.setDefaults(
            hashMapOf(
                "Referrals_Enabled" to false,
                "Referrals_Incentive" to DEFAULT_INCENTIVE
            )
        )
        firebaseRemoteConfig
            .fetch()
            .addOnSuccessListener {
                firebaseRemoteConfig.activateFetched()
            }
    }

    val referralsEnabled
        get() = firebaseRemoteConfig.getBoolean("Referrals_Enabled")

    val referralsIncentiveAmount
        get() = firebaseRemoteConfig.getLong("Referrals_Incentive")

    val referralsIosBundleId
        get() = firebaseRemoteConfig.getString("DynamicLink_iOS_BundleId")

    val referralsDomain
        get() = firebaseRemoteConfig.getString("DynamicLink_Domain_Prefix")

    //val linkDomain
}
