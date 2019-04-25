package com.hedvig.android.owldroid.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.hedvig.android.owldroid.ui.claims.ClaimsViewModel
import com.hedvig.android.owldroid.ui.common.DirectDebitViewModel
import com.hedvig.android.owldroid.ui.dashboard.DashboardViewModel
import com.hedvig.android.owldroid.ui.marketing.MarketingStoriesViewModel
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MarketingStoriesViewModel::class)
    abstract fun marketingStoriesViewModel(viewModel: MarketingStoriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun profileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ClaimsViewModel::class)
    abstract fun claimsViewModel(viewModel: ClaimsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DirectDebitViewModel::class)
    abstract fun directDebitViewModel(viewModel: DirectDebitViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun dashboardViewModel(dashboardViewModel: DashboardViewModel): ViewModel
}
