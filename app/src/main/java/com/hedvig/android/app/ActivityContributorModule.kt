package com.hedvig.android.app

import com.hedvig.android.app.profile.MyInfoActivity
import com.hedvig.android.app.profile.PaymentActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityContributorModule {
    @ContributesAndroidInjector
    abstract fun contributeMarketingActivity(): MarketingActivity

    @ContributesAndroidInjector
    abstract fun contributeDebugActivity(): DebugActivity

    @ContributesAndroidInjector
    abstract fun contributeLogoActivity(): LogoActivity

    @ContributesAndroidInjector
    abstract fun contributeProfileActivity(): ProfileActivity

    @ContributesAndroidInjector
    abstract fun contributeProfileMyInfoActivity(): MyInfoActivity

    @ContributesAndroidInjector
    abstract fun contributeProfilePaymentActivity(): PaymentActivity
}