package com.hedvig.android.owldroid.ui.claims.pledge

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.ui.common.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_honesty_pledge.*

class HonestyPledgeBottomSheet: RoundedBottomSheetDialogFragment() {

    override fun getTheme() = R.style.NoTitleBottomSheetDialogTheme

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_honesty_pledge, null)
        dialog.setContentView(view)

        dialog.bottomSheetHonestyPledgeButton.setOnClickListener {
            Toast.makeText(requireContext(), "Navigate to chat with the key'${arguments?.getString(ARGS_CLAIM_KEY)}'!", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val ARGS_CLAIM_KEY = "claim_key"

        fun newInstance(claimKey: String): HonestyPledgeBottomSheet {
            val arguments = Bundle().apply {
                putString(ARGS_CLAIM_KEY, claimKey)
            }

            return HonestyPledgeBottomSheet().also { it.arguments = arguments }
        }
    }
}
