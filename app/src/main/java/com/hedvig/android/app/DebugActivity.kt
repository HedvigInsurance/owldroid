package com.hedvig.android.app

import android.content.Context
import android.os.Bundle
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.android.support.DaggerAppCompatActivity

class DebugActivity : DaggerAppCompatActivity() {
/*    private lateinit var profileBroadcastReceiver: BroadcastReceiver
    private lateinit var marketingBroadcastReceiver: BroadcastReceiver*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
    }

    override fun onStart() {
        super.onStart()
        FirebaseDynamicLinks
            .getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let { link ->
                    link.getQueryParameter("memberId")?.let { referee ->
                        getSharedPreferences("debug", Context.MODE_PRIVATE)
                            .edit()
                            .putString("referee", referee)
                            .apply()
                    }
                }
            }
    }

/*    override fun onPause() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.unregisterReceiver(profileBroadcastReceiver)
        localBroadcastManager.unregisterReceiver(marketingBroadcastReceiver)
        super.onPause()
    }*/

/*    override fun onResume() {
        val navigationController = findNavController(R.id.navigationHostFragment)

        profileBroadcastReceiver = newBroadcastReceiver { _, intent ->
            when (intent?.getStringExtra("action")) {
                "my_info" -> navigationController.navigate(R.id.action_profileFragment_to_myInfoFragment)
                "my_home" -> navigationController.navigate(R.id.action_profileFragment_to_myHomeFragment)
                "coinsured" -> navigationController.navigate(R.id.action_profileFragment_to_coinsuredFragment)
                "charity" -> navigationController.navigate(R.id.action_profileFragment_to_charityFragment)
                "payment" -> navigationController.navigate(R.id.action_profileFragment_to_paymentFragment)
                "trustly" -> navigationController.navigate(R.id.action_paymentFragment_to_trustlyFragment)
                "feedback" -> navigationController.navigate(R.id.action_profileFragment_to_feedbackFragment)
                "about_app" -> navigationController.navigate(R.id.action_profileFragment_to_aboutAppFragment)
                "licenses" -> navigationController.navigate(R.id.action_aboutAppFragment_to_licensesFragment)
                "referrals" -> navigationController.navigate(R.id.action_profileFragment_to_referralFragment)
                "back" -> navigationController.popBackStack()
                "logout" -> navigationController.popBackStack()
            }
        }

        marketingBroadcastReceiver = newBroadcastReceiver { _, _ ->
            navigationController.popBackStack()
        }
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(profileBroadcastReceiver, IntentFilter("profileNavigation"))
        localBroadcastManager.registerReceiver(marketingBroadcastReceiver, IntentFilter("marketingResult"))
        super.onResume()
    }*/
}
