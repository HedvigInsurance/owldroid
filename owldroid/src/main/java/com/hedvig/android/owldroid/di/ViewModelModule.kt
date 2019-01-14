package com.hedvig.android.owldroid.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.hedvig.android.owldroid.ui.marketing.MarketingStoriesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MarketingStoriesViewModel::class)
    internal abstract fun marketingStoriesViewModel(viewModel: MarketingStoriesViewModel): ViewModel
}
