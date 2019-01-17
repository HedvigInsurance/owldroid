package com.hedvig.android.app.profile

import android.os.Bundle
import android.widget.FrameLayout
import com.hedvig.android.owldroid.ui.profile.myinfo.MyInfoFragment
import com.hedvig.android.owldroid.R
import dagger.android.support.DaggerAppCompatActivity

class MyInfoActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = object : FrameLayout(this) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(
                    R.id.profile_member_info_screen_content,
                    MyInfoFragment()
                )
                transaction.commitAllowingStateLoss()
            }
        }
        content.id = R.id.profile_member_info_screen_content
        setContentView(content)
    }
}
