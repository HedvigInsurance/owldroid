package com.hedvig.android.owldroid.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.annotation.DrawableRes
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.ui.common.RoundedBottomSheetDialogFragment

class PerilBottomSheet : RoundedBottomSheetDialogFragment() {
    @SuppressLint("InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = layoutInflater.inflate(R.layout.peril_bottom_sheet, null)
        dialog.setContentView(view)
    }

    companion object {
        private const val PERIL_ICON = "peril_icon"
        private const val PERIL_TITLE = "peril_title"
        private const val PERIL_DESCRIPTION = "peril_description"

        fun newInstance(@DrawableRes icon: Int, title: String, description: String): PerilBottomSheet {
            val arguments = Bundle().apply {
                putInt(PERIL_ICON, icon)
                putString(PERIL_TITLE, title)
                putString(PERIL_DESCRIPTION, description)
            }

            return PerilBottomSheet().also { it.arguments = arguments }
        }
    }
}
