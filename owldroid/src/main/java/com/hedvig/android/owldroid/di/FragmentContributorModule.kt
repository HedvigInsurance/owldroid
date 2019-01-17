package com.hedvig.android.owldroid.di

import com.hedvig.android.owldroid.ui.marketing.MarketingFragment
import com.hedvig.android.owldroid.ui.marketing.StoryFragment
import com.hedvig.android.owldroid.ui.profile.ProfileFragment
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
}
