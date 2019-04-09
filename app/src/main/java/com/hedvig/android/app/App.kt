package com.hedvig.android.app

import android.content.Context
import com.hedvig.android.owldroid.service.RemoteConfig
import com.hedvig.android.owldroid.service.TextKeys
import com.ice.restring.Restring
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject
import android.support.multidex.MultiDex

class App : DaggerApplication() {

    @Inject
    lateinit var textKeys: TextKeys

    @Inject
    lateinit var remoteConfig: RemoteConfig

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
        try {
            Restring.init(this)
            textKeys.refreshTextKeys()
        } catch (e: Exception) {
            Timber.e(e)
        } catch (e: NoClassDefFoundError) {
            Timber.e(e)
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}
