package com.hedvig.android.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout

class ReferralAcceptanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.referralAcceptanceFragment, ReferralAcceptanceFragment())
                    commitAllowingStateLoss()
                }
            }
        }
        view.id = R.id.referralAcceptanceFragment
        setContentView(view)
    }
}
