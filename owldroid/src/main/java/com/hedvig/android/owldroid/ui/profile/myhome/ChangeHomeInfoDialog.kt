package com.hedvig.android.owldroid.ui.profile.myhome

import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.util.whenApiVersion
import kotlinx.android.synthetic.main.dialog_change_home_info.*

class ChangeHomeInfoDialog : DialogFragment() {

    val navController by lazy { requireActivity().findNavController(R.id.rootNavigationHost) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_change_home_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whenApiVersion(Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = 2f
        }

        dialogCancel.setOnClickListener {
            dismiss()
        }

        dialogConfirm.setOnClickListener {
            dismiss()
            navController.navigate(R.id.action_myHomeFragment_to_chatFragment)
            // localBroadcastManager.sendBroadcast(Intent(ProfileFragment.PROFILE_NAVIGATION_BROADCAST).apply {
            //     putExtra("action", "chat")
            // })
        }
    }
}
