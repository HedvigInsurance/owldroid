package com.hedvig.android.owldroid.ui.profile.aboutapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_about_app.*
import javax.inject.Inject
import javax.inject.Named

class AboutAppFragment : Fragment() {

    @Inject
    @field:Named("VERSION_NUMBER")
    lateinit var appVersion: String

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var profileViewModel: ProfileViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.rootNavigationHost)
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

        setupLargeTitle(R.string.PROFILE_ABOUT_APP_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            navController.popBackStack()
        }

        licenseAttributions.setOnClickListener {
            navController.navigate(R.id.action_aboutAppFragment_to_licensesFragment)
        }

        versionNumber.text = interpolateTextKey(
            resources.getString(R.string.PROFILE_ABOUT_APP_VERSION),
            "VERSION_NUMBER" to appVersion
        )

        profileViewModel.data.observe(this, Observer { data ->
            data?.member()?.id()?.let { id ->
                memberId.text = interpolateTextKey(
                    resources.getString(R.string.PROFILE_ABOUT_APP_MEMBER_ID),
                    "MEMBER_ID" to id
                )
            }
        })
    }
}
