package com.hedvig.android.owldroid.ui.profile.myhome

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.whenApiVersion
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.dialog_change_home_info.*
import javax.inject.Inject

class ChangeHomeInfoDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var profileViewModel: ProfileViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        }
    }

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
            profileViewModel.triggerFreeTextChat {
                dismiss()
                navController.navigate(R.id.action_myHomeFragment_to_chatFragment)
            }
        }
    }
}
