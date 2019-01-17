package com.hedvig.android.app.profile

import android.os.Bundle
import android.widget.FrameLayout
import com.hedvig.android.app.R
import com.hedvig.android.owldroid.ui.profile.payment.PaymentFragment
import dagger.android.support.DaggerAppCompatActivity

class PaymentActivity: DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                        R.id.profile_payment_screen_content,
                        PaymentFragment()
                )
                transaction.commitAllowingStateLoss()
            }
        }
        content.id = R.id.profile_payment_screen_content
        setContentView(content)
    }
}
