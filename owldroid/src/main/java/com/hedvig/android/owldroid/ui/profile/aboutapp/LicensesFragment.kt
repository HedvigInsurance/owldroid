package com.hedvig.android.owldroid.ui.profile.aboutapp

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_licenses.*

class LicensesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_licenses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = "Licensrättigheter"
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", "back")
            localBroadcastManager.sendBroadcast(intent)
        }

        webView.loadUrl("file:///android_asset/open_source_licenses.html")
    }
}
