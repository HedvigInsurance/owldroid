package com.hedvig.android.app

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


class HedvigApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent
                .builder()
                .create(this)
                .build()
    }
}