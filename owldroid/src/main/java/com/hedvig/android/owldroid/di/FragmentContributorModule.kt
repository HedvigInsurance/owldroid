package com.hedvig.android.owldroid.di

import com.hedvig.android.owldroid.ui.claims.ClaimsFragment
import com.hedvig.android.owldroid.ui.claims.commonclaim.CommonClaimFragment
import com.hedvig.android.owldroid.ui.claims.commonclaim.EmergencyFragment
import com.hedvig.android.owldroid.ui.claims.pledge.HonestyPledgeBottomSheet
import com.hedvig.android.owldroid.ui.dashboard.DashboardFragment
import com.hedvig.android.owldroid.ui.marketing.MarketingFragment
import com.hedvig.android.owldroid.ui.marketing.StoryFragment
import com.hedvig.android.owldroid.ui.profile.ProfileFragment
import com.hedvig.android.owldroid.ui.profile.aboutapp.AboutAppFragment
import com.hedvig.android.owldroid.ui.profile.charity.CharityFragment
import com.hedvig.android.owldroid.ui.profile.coinsured.CoinsuredFragment
import com.hedvig.android.owldroid.ui.profile.myhome.ChangeHomeInfoDialog
import com.hedvig.android.owldroid.ui.profile.myhome.MyHomeFragment
import com.hedvig.android.owldroid.ui.profile.myinfo.MyInfoFragment
import com.hedvig.android.owldroid.ui.profile.payment.PaymentFragment
import com.hedvig.android.owldroid.ui.profile.payment.TrustlyFragment
import com.hedvig.android.owldroid.ui.profile.referral.ReferralFragment
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
