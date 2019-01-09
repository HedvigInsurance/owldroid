package com.hedvig.android.app

import android.os.Bundle
import android.widget.FrameLayout
import dagger.android.support.DaggerAppCompatActivity

class MarketingActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.marketing_screen_content, MarketingFragment())
                transaction.commitAllowingStateLoss()
            }
        }


        content.setId(R.id.marketing_screen_content)
        setContentView(content)
    }
}

