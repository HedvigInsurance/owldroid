package com.hedvig.android.owldroid.ui.profile.feedback

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.whenApiVersion
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_feedback, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_FEEDBACK_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            requireActivity().findNavController(R.id.loggedInNavigationHost).popBackStack()
        }

        bugReportEmail.setOnClickListener {
            startActivity(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${requireContext().getString(R.string.bug_report_mail)}?subject=Buggrapport")
            })
        }

        playLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}"))
            var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            whenApiVersion(Build.VERSION_CODES.LOLLIPOP) {
                flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            }
            startActivity(intent)
        }
    }
}
