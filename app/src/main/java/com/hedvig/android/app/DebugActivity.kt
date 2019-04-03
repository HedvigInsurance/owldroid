package com.hedvig.android.app

import android.content.Context
import android.os.Bundle
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.android.support.DaggerAppCompatActivity

class DebugActivity : DaggerAppCompatActivity() {
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
}
