package com.hedvig.android.app

import com.hedvig.android.owldroid.di.FragmentContributorModule
import com.hedvig.android.owldroid.di.OwldroidModule
import com.hedvig.android.owldroid.di.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidSupportInjectionModule::class, AppModule::class, OwldroidModule::class, ViewModelModule::class, ActivityContributorModule::class, FragmentContributorModule::class])
interface AppComponent: AndroidInjector<App> {
    override fun inject(app: App)

    @Component.Builder
    abstract class Builder: AndroidInjector.Builder<App>()
}
