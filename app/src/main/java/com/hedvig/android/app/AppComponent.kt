package com.hedvig.android.app

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityContributorModule::class])
interface AppComponent : AndroidInjector<App> {
    override fun inject(app: App)

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}