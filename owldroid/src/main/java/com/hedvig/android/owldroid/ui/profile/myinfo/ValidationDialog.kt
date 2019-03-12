package com.hedvig.android.owldroid.ui.profile.myinfo

import android.os.Bundle
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
            dialogTitle.text = args.getString("title")
            dialogParagraph.text = args.getString("paragraph")
            dialogConfirm.text = args.getString("dismiss")

            dialogConfirm.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    companion object {
        fun newInstance(title: String, paragraph: String, dismiss: String): ValidationDialog {
            val dialog = ValidationDialog()
            val arguments = Bundle()
            arguments.apply {
                putString("title", title)
                putString("paragraph", paragraph)
                putString("dismiss", dismiss)
            }
            dialog.arguments = arguments
            return dialog
        }
    }
}
