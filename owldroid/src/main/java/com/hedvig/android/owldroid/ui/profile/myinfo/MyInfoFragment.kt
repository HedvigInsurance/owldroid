package com.hedvig.android.owldroid.ui.profile.myinfo

import android.content.Context
import android.support.v4.app.Fragment
import dagger.android.support.AndroidSupportInjection

class MyInfoFragment : Fragment() {
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}
