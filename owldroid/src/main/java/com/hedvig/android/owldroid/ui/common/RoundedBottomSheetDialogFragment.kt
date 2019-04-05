package com.hedvig.android.owldroid.ui.common

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import com.hedvig.android.owldroid.R
import android.view.LayoutInflater
import com.hedvig.android.owldroid.util.extensions.show
import timber.log.Timber


class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun setupDialog(dialog: Dialog, style: Int) {
        arguments?.getInt(ARGS_LAYOUT_KEY)?.let { layout ->
            val view = LayoutInflater.from(context).inflate(layout, null)
            view.show()
            dialog.setContentView(view)
        } ?: Timber.e("Started bottom sheet dialog without layout res!")
    }

    companion object {
        private const val ARGS_LAYOUT_KEY = "args_layout_key"

        fun newInstance(@LayoutRes layout: Int) = RoundedBottomSheetDialogFragment().also { fragment ->
            val args = Bundle()
            args.putInt(ARGS_LAYOUT_KEY, layout)
            fragment.arguments = args
        }
    }
}
