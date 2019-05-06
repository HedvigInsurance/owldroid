package com.hedvig.android.owldroid.di

import com.hedvig.android.owldroid.feature.claims.ui.ClaimsFragment
import com.hedvig.android.owldroid.feature.claims.ui.commonclaim.CommonClaimFragment
import com.hedvig.android.owldroid.feature.claims.ui.commonclaim.EmergencyFragment
import com.hedvig.android.owldroid.feature.claims.ui.pledge.HonestyPledgeBottomSheet
import com.hedvig.android.owldroid.feature.dashboard.ui.DashboardFragment
import com.hedvig.android.owldroid.feature.marketing.ui.MarketingFragment
import com.hedvig.android.owldroid.feature.marketing.ui.StoryFragment
import com.hedvig.android.owldroid.feature.profile.ui.ProfileFragment
import com.hedvig.android.owldroid.feature.profile.ui.aboutapp.AboutAppFragment
import com.hedvig.android.owldroid.feature.profile.ui.charity.CharityFragment
import com.hedvig.android.owldroid.feature.profile.ui.coinsured.CoinsuredFragment
import com.hedvig.android.owldroid.feature.profile.ui.myhome.ChangeHomeInfoDialog
import com.hedvig.android.owldroid.feature.profile.ui.myhome.MyHomeFragment
import com.hedvig.android.owldroid.feature.profile.ui.myinfo.MyInfoFragment
import com.hedvig.android.owldroid.feature.profile.ui.payment.PaymentFragment
import com.hedvig.android.owldroid.feature.profile.ui.payment.TrustlyFragment
import com.hedvig.android.owldroid.feature.profile.ui.referral.ReferralFragment
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
    abstract fun contributeTrustlyFragment(): TrustlyFragment

    @ContributesAndroidInjector
    abstract fun contributeMyHomeFragment(): MyHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeCoinsuredFragment(): CoinsuredFragment

    @ContributesAndroidInjector
    abstract fun contributeCharityFragment(): CharityFragment

    @ContributesAndroidInjector
    abstract fun contributeReferralFragment(): ReferralFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutAppFragment(): AboutAppFragment

    @ContributesAndroidInjector
    abstract fun contributeClaimsFragment(): ClaimsFragment

    @ContributesAndroidInjector
    abstract fun contributeCommonClaimFragment(): CommonClaimFragment

    @ContributesAndroidInjector
    abstract fun contributeEmergencyFragment(): EmergencyFragment

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeHonestyPledgeBottomSheet(): HonestyPledgeBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeChangeHomeInfoDialog(): ChangeHomeInfoDialog
}
