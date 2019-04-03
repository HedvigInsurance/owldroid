package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.util.NavigationAnalytics
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import com.hedvig.android.owldroid.util.newBroadcastReceiver
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.loading_spinner.*
import javax.inject.Inject

class ProfileFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var profileViewModel: ProfileViewModel

    private var broadcastReceiver: BroadcastReceiver? = null

    private var navigationAnalytics: NavigationAnalytics? = null

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.profileNavigationHost)
    }

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
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navigationAnalytics != null) {
            navigationAnalytics?.let { navController.addOnDestinationChangedListener(it) }
        } else {
            broadcastReceiver = newBroadcastReceiver { _, _ ->
                if (navigationAnalytics == null) {
                    navigationAnalytics = NavigationAnalytics(requireActivity())
                }

                navigationAnalytics?.let { navController.addOnDestinationChangedListener(it) }
            }
            broadcastReceiver?.let {
                localBroadcastManager.registerReceiver(
                    it,
                    IntentFilter("profileScreenDidAppear")
                )
            }
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.PROFILE_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))

        populateData()
        loadReferralFeature()
    }

    private fun loadReferralFeature() {
        profileViewModel.remoteConfigData.observe(this, Observer { remoteConfigData ->
            remoteConfigData?.let { rcd ->
                if (!rcd.referralsEnabled) {
                    return@Observer
                }
                profileReferralRow.setHighlighted()
                profileReferralRow.name = interpolateTextKey(
                    resources.getString(R.string.PROFILE_ROW_REFERRAL_TITLE),
                    hashMapOf("INCENTIVE" to "${rcd.referralsIncentiveAmount}")
                )
                profileReferralRow.setOnClickListener {
                    navController.navigate(R.id.action_profileFragment_to_referralFragment)
                }
                profileReferralRow.show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navigationAnalytics?.let { navController.removeOnDestinationChangedListener(it) }
        broadcastReceiver?.let { localBroadcastManager.unregisterReceiver(it) }
    }

    private fun populateData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            rowContainer.show()
            logout.show()

            profileData?.let { data ->
                setupMyInfoRow(data)
                setupMyHomeRow(data)
                setupCoinsured(data)
                setupCharity(data)
                setupPayment(data)
                setupCertificateUrl(data)
            }

            feedbackRow.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_feedbackFragment)
            }
            aboutAppRow.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_aboutAppFragment)
            }
            logout.setOnClickListener {
                profileViewModel.logout {
                    localBroadcastManager.sendBroadcast(Intent(PROFILE_NAVIGATION_BROADCAST).apply {
                        putExtra("action", "logout")
                    })
                }
            }
        })
    }

    private fun setupMyInfoRow(profileData: ProfileQuery.Data) {
        val firstName = profileData.member().firstName() ?: ""
        val lastName = profileData.member().lastName() ?: ""
        myInfoRow.description = "$firstName $lastName"
        myInfoRow.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_myInfoFragment)
        }
    }

    private fun setupMyHomeRow(profileData: ProfileQuery.Data) {
        myHomeRow.description = profileData.insurance().address()
        myHomeRow.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_myHomeFragment)
        }
    }

    private fun setupCoinsured(profileData: ProfileQuery.Data) {
        val personsInHousehold = profileData.insurance().personsInHousehold() ?: 1
        coinsuredRow.description = interpolateTextKey(
            resources.getString(R.string.PROFILE_ROW_COINSURED_DESCRIPTION),
            hashMapOf("NUMBER" to "$personsInHousehold")
        )
        coinsuredRow.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_coinsuredFragment)
        }
    }

    private fun setupCharity(profileData: ProfileQuery.Data) {
        charityRow.description = profileData.cashback()?.name()
        charityRow.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_charityFragment)
        }
    }

    private fun setupPayment(profileData: ProfileQuery.Data) {
        paymentRow.description = interpolateTextKey(
            resources.getString(R.string.PROFILE_ROW_PAYMENT_DESCRIPTION),
            hashMapOf("COST" to profileData.insurance().monthlyCost()?.toString())
        )
        paymentRow.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_paymentFragment)
        }
    }

    private fun setupCertificateUrl(profileData: ProfileQuery.Data) {
        profileData.insurance().certificateUrl()?.let { policyUrl ->
            insuranceCertificateRow.show()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
            insuranceCertificateRow.setOnClickListener {
                startActivity(intent)
            }
        }
    }

    companion object {
        const val PROFILE_NAVIGATION_BROADCAST = "profileNavigation"
    }
}
