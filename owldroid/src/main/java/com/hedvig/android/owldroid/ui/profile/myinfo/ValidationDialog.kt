package com.hedvig.android.owldroid.ui.profile.myinfo

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import kotlinx.android.synthetic.main.dialog_validation.*

class ValidationDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_validation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            dialogTitle.text = resources.getString(args.getInt("title"))
            dialogParagraph.text = resources.getString(args.getInt("paragraph"))
            dialogConfirm.text = resources.getString(args.getInt("dismiss"))

            dialogConfirm.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    companion object {
        fun newInstance(@StringRes title: Int, @StringRes paragraph: Int, @StringRes dismiss: Int): ValidationDialog {
            val dialog = ValidationDialog()
            val arguments = Bundle()
            arguments.apply {
                putInt("title", title)
                putInt("paragraph", paragraph)
                putInt("dismiss", dismiss)
            }
            dialog.arguments = arguments
            return dialog
        }
    }
}
