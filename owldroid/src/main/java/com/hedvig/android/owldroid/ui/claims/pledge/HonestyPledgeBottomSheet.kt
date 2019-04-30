package com.hedvig.android.owldroid.ui.claims.pledge

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.claims.ClaimsViewModel
import com.hedvig.android.owldroid.ui.common.RoundedBottomSheetDialogFragment
import com.hedvig.android.owldroid.util.extensions.view.setHapticClickListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.bottom_sheet_honesty_pledge.*
import javax.inject.Inject

class HonestyPledgeBottomSheet : RoundedBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var claimsViewModel: ClaimsViewModel

    val navController by lazy { requireActivity().findNavController(R.id.rootNavigationHost) }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        claimsViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ClaimsViewModel::class.java)
        }
    }

    override fun getTheme() = R.style.NoTitleBottomSheetDialogTheme

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_honesty_pledge, null)
        dialog.setContentView(view)

        dialog.bottomSheetHonestyPledgeButton.setHapticClickListener {
            claimsViewModel.triggerClaimsChat {
                dismiss()
                arguments?.getInt(ARGS_NAVIGATION_ACTION)?.let { navController.navigate(it) }
            }
        }
    }

    companion object {
        private const val ARGS_CLAIM_KEY = "claim_key"
        private const val ARGS_NAVIGATION_ACTION = "navigation_action"

        fun newInstance(claimKey: String, @IdRes navigationAction: Int): HonestyPledgeBottomSheet {
            val arguments = Bundle().apply {
                putString(ARGS_CLAIM_KEY, claimKey)
                putInt(ARGS_NAVIGATION_ACTION, navigationAction)
            }

            return HonestyPledgeBottomSheet().also { it.arguments = arguments }
        }
    }
}
