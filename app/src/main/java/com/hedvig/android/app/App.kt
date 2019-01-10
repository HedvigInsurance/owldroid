package com.hedvig.android.app

import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class App : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()

        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
    }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        throw NotImplementedError()
        //return DaggerAppComponent.builder().create(this)
    }
}