package com.hedvig.android.app

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentContributorModule {
    @ContributesAndroidInjector
    abstract fun contributeStoryFragment(): StoryFragment

    @ContributesAndroidInjector
    abstract fun contributeMarketingFragment(): MarketingFragment
}
