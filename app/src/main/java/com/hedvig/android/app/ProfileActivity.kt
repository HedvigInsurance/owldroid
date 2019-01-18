package com.hedvig.android.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.widget.FrameLayout
import com.hedvig.android.app.profile.MyInfoActivity
import com.hedvig.android.app.profile.PaymentActivity
import com.hedvig.android.owldroid.ui.profile.ProfileFragment
import com.hedvig.android.owldroid.R
import dagger.android.support.DaggerAppCompatActivity

class ProfileActivity : DaggerAppCompatActivity() {
    private lateinit var broadcastReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                    R.id.profile_screen_content,
                    ProfileFragment()
                )
                transaction.commitAllowingStateLoss()
            }
        }
        content.id = R.id.profile_screen_content
        setContentView(content)

    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    override fun onResume() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getStringExtra("subscreen")) {
                    "my_info" -> startActivity(Intent(this@ProfileActivity, MyInfoActivity::class.java))
                    "payment" -> startActivity(Intent(this@ProfileActivity, PaymentActivity::class.java))
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("profileNavigation"))
        super.onResume()
    }
}
