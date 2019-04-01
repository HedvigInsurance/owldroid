package com.hedvig.android.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.hedvig.android.owldroid.ui.claims.ClaimsFragment

class ClaimsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.claimsScreen, ClaimsFragment())
                    .commitAllowingStateLoss()
            }
        }

        content.id = R.id.claimsScreen
        setContentView(content)
    }
}
