package com.hedvig.android.app

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            ApplicationModule::class,
            ActivitiesBindingModule::class
        ]
)
interface ApplicationComponent: AndroidInjector<HedvigApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun create(app: HedvigApplication): Builder
        fun build(): ApplicationComponent
    }
}