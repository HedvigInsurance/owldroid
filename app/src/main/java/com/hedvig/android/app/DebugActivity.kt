package com.hedvig.android.app

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.util.newBroadcastReceiver
import dagger.android.support.DaggerAppCompatActivity

class DebugActivity : DaggerAppCompatActivity() {
    private lateinit var profileBroadcastReceiver: BroadcastReceiver
    private lateinit var marketingBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

    }

    override fun onPause() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.unregisterReceiver(profileBroadcastReceiver)
        localBroadcastManager.unregisterReceiver(marketingBroadcastReceiver)
        super.onPause()
    }

    override fun onResume() {
        val navigationController = findNavController(R.id.navigationHostFragment)

        profileBroadcastReceiver = newBroadcastReceiver { _, intent ->
            when (intent?.getStringExtra("action")) {
                "my_info" -> navigationController.navigate(R.id.action_profileFragment_to_myInfoFragment)
                "my_home" -> navigationController.navigate(R.id.action_profileFragment_to_myHomeFragment)
                "coinsured" -> navigationController.navigate(R.id.action_profileFragment_to_coinsuredFragment)
                "charity" -> navigationController.navigate(R.id.action_profileFragment_to_charityFragment)
                "payment" -> navigationController.navigate(R.id.action_profileFragment_to_paymentFragment)
                "back" -> navigationController.popBackStack()
            }
        }

        marketingBroadcastReceiver = newBroadcastReceiver { _, _ ->
            navigationController.popBackStack()
        }
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(profileBroadcastReceiver, IntentFilter("profileNavigation"))
        localBroadcastManager.registerReceiver(marketingBroadcastReceiver, IntentFilter("marketingResult"))
        super.onResume()
    }
}