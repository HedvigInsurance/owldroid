package com.hedvig.android.owldroid.di

import com.hedvig.android.owldroid.ui.marketing.MarketingFragment
import com.hedvig.android.owldroid.ui.marketing.StoryFragment
import com.hedvig.android.owldroid.ui.profile.ProfileFragment
import com.hedvig.android.owldroid.ui.profile.myhome.MyHomeFragment
import com.hedvig.android.owldroid.ui.profile.myinfo.MyInfoFragment
import com.hedvig.android.owldroid.ui.profile.payment.PaymentFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentContributorModule {
    @ContributesAndroidInjector
    abstract fun contributeStoryFragment(): StoryFragment

    @ContributesAndroidInjector
    abstract fun contributeMarketingFragment(): MarketingFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeMyInfoFragment(): MyInfoFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFragment(): PaymentFragment

    @ContributesAndroidInjector
    abstract fun contributeMyHomeFragment(): MyHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeFeedbackFragment(): FeedbackFragment

    @ContributesAndroidInjector
    abstract fun contributeCoinsuredFragment(): CoinsuredFragment

    @ContributesAndroidInjector
    abstract fun contributeCharityFragment(): CharityFragment
}
