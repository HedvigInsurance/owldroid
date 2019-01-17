package com.hedvig.android.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.widget.FrameLayout
import com.hedvig.android.app.profile.MyInfoActivity
import com.hedvig.android.owldroid.ui.profile.ProfileFragment
import com.hedvig.android.owldroid.R
import dagger.android.support.DaggerAppCompatActivity

class ProfileActivity : DaggerAppCompatActivity() {
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

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                startActivity(Intent(this@ProfileActivity, MyInfoActivity::class.java))
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("profileMyInfo"))
    }
}
