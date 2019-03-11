package com.hedvig.android.owldroid.ui.profile.aboutapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.BuildConfig
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_about_app.*
import javax.inject.Inject

class AboutAppFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var profileViewModel: ProfileViewModel

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_about_app, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.about_app_title)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", "back")
            localBroadcastManager.sendBroadcast(intent)
        }

        licenseAttributions.setOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", "licenses")
            localBroadcastManager.sendBroadcast(intent)
        }

        versionNumber.text = "Version: ${BuildConfig.VERSION_NAME}"

        profileViewModel.data.observe(this, Observer { data ->
            data?.member()?.id()?.let { id ->
                memberId.text = "Medlemsnummer: $id"
            }
        })
    }
}
