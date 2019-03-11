package com.hedvig.android.app

import com.hedvig.android.owldroid.service.TextKeys
import com.ice.restring.Restring
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var textKeys: TextKeys

    override fun onCreate() {
        super.onCreate()

        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
        Restring.init(this)
        textKeys.refreshTextKeys()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}