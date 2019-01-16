package com.hedvig.android.app

import android.content.Intent
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_debug.*

class DebugActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        debug_open_marketing.setOnClickListener {
            val intent = Intent(this, MarketingActivity::class.java)
            startActivity(intent)
        }

        debug_open_logo.setOnClickListener {
            val intent = Intent(this, LogoActivity::class.java)
            startActivity(intent)
        }
    }

}