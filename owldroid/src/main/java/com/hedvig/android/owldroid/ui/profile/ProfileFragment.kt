package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.service.RemoteConfig
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var remoteConfig: RemoteConfig

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var navController: NavController

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

        navController = requireActivity().findNavController(R.id.profileNavigationHost)

        collapsingToolbar.title = resources.getString(R.string.PROFILE_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))

        if (remoteConfig.referralsEnabled) {
            profileReferralRow.setHighlighted()
            profileReferralRow.name = interpolateTextKey(
                resources.getString(R.string.PROFILE_ROW_REFERRAL_TITLE),
                hashMapOf("INCENTIVE" to "${remoteConfig.referralsIncentiveAmount}")
            )
            profileReferralRow.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_referralFragment)
            }
            profileReferralRow.show()
        }

        populateData()
    }

    private fun populateData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            profile_loading_spinner.remove()
            profile_rows_container.show()
            profile_log_out_button.show()

            profileData?.let { data ->
                setupMyInfoRow(data)
                setupMyHomeRow(data)
                setupCoinsured(data)
                setupCharity(data)
                setupPayment(data)
                setupPolicyRow(data)
            }

            profile_feedback.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_feedbackFragment)
            }
            profile_about_app.setOnClickListener {
                navController.navigate(R.id.action_profileFragment_to_aboutAppFragment)
            }
            profile_log_out_button.setOnClickListener {
                // TODO Make some native navigation module and do stuff here
            }
        })
    }

    private fun setupMyInfoRow(profileData: ProfileQuery.Data) {
        val firstName = profileData.member().firstName() ?: ""
        val lastName = profileData.member().lastName() ?: ""
        profile_my_info_row.description = "$firstName $lastName"
        profile_my_info_row.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_myInfoFragment)
        }
    }

    private fun setupMyHomeRow(profileData: ProfileQuery.Data) {
        profile_my_home_row.description = profileData.insurance().address()
        profile_my_home_row.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_myHomeFragment)
        }
    }

    private fun setupCoinsured(profileData: ProfileQuery.Data) {
        val personsInHousehold = profileData.insurance().personsInHousehold() ?: 1
        profile_coinsured_row.description = interpolateTextKey(
            resources.getString(R.string.PROFILE_ROW_COINSURED_DESCRIPTION),
            hashMapOf("NUMBER" to "$personsInHousehold")
        )
        profile_coinsured_row.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_coinsuredFragment)
        }
    }

    private fun setupCharity(profileData: ProfileQuery.Data) {
        profile_charity_row.description = profileData.cashback()?.name()
        profile_charity_row.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_charityFragment)
        }
    }

    private fun setupPayment(profileData: ProfileQuery.Data) {
        profile_payment_row.description = interpolateTextKey(
            resources.getString(R.string.PROFILE_ROW_PAYMENT_DESCRIPTION),
            hashMapOf("COST" to profileData.insurance().monthlyCost()?.toString())
        )
        profile_payment_row.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_paymentFragment)
        }
    }

    private fun setupPolicyRow(profileData: ProfileQuery.Data) {
        profileData.insurance().policyUrl()?.let { policyUrl ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
            profile_insurance_certificate_row.setOnClickListener {
                startActivity(intent)
            }
        }
    }
}
