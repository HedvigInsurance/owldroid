package com.hedvig.android.app

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.ui.marketing.MarketingFragment
import com.ice.restring.Restring
import dagger.android.support.DaggerAppCompatActivity

class MarketingActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                        R.id.marketing_screen_content,
                        MarketingFragment()
                )
                transaction.commitAllowingStateLoss()
            }
        }


        content.id = R.id.marketing_screen_content
        setContentView(content)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(Restring.wrapContext(newBase))
    }
}

