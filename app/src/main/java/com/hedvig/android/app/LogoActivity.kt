package com.hedvig.android.app

import android.os.Bundle
import android.widget.FrameLayout
import com.hedvig.android.owldroid.ui.logo.LogoFragment
import dagger.android.support.DaggerAppCompatActivity

class LogoActivity: DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = object: FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()

                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                        R.id.logo_content,
                        LogoFragment()
                )
                transaction.commitAllowingStateLoss()
            }
        }

        content.id = R.id.logo_content
        setContentView(content)
    }
}