package com.hedvig.android.app

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityContributorModule {
    @ContributesAndroidInjector
    abstract fun contributeDebugActivity(): DebugActivity

    @ContributesAndroidInjector
    abstract fun contributeDebugFragment(): DebugFragment

    @ContributesAndroidInjector
    abstract fun contributeReferralAcceptanceFragment(): ReferralAcceptanceFragment
}
