package com.hedvig.android.app

import android.os.Bundle
import android.widget.FrameLayout
import com.hedvig.android.owldroid.ui.profile.ProfileFragment
import dagger.android.support.DaggerAppCompatActivity

class ProfileActivity: DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = object: FrameLayout(this) {
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
}