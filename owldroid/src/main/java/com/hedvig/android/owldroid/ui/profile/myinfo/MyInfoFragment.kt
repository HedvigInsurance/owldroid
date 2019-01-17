package com.hedvig.android.owldroid.ui.profile.myinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import dagger.android.support.AndroidSupportInjection

class MyInfoFragment : Fragment() {
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.activity_my_info,
            container,
            false
        )
    }
}
