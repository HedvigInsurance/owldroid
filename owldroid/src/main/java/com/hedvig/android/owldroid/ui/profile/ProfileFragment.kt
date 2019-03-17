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
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var profileViewModel: ProfileViewModel

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

        collapsingToolbar.title = resources.getString(R.string.PROFILE_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))

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

            attachNavigationOnClick(profile_feedback, "feedback")
            attachNavigationOnClick(profileReferralRow, "referrals")
            attachNavigationOnClick((profile_about_app), "about_app")
            attachNavigationOnClick(profile_log_out_button, "logout")
        })
    }

    private fun setupMyInfoRow(profileData: ProfileQuery.Data) {
        val firstName = profileData.member().firstName() ?: ""
        val lastName = profileData.member().lastName() ?: ""
        profile_my_info_row.description = "$firstName $lastName"
        attachNavigationOnClick(profile_my_info_row, "my_info")
    }

    private fun setupMyHomeRow(profileData: ProfileQuery.Data) {
        profile_my_home_row.description = profileData.insurance().address()
        attachNavigationOnClick(profile_my_home_row, "my_home")
    }

    private fun setupCoinsured(profileData: ProfileQuery.Data) {
        val personsInHousehold = profileData.insurance().personsInHousehold() ?: 1
        profile_coinsured_row.description = "$personsInHousehold medförsäkrade"
        attachNavigationOnClick(profile_coinsured_row, "coinsured")
    }

    private fun setupCharity(profileData: ProfileQuery.Data) {
        profile_charity_row.description = profileData.cashback()?.name()
        attachNavigationOnClick(profile_charity_row, "charity")
    }

    private fun setupPayment(profileData: ProfileQuery.Data) {
        profile_payment_row.description =
            "${profileData.insurance().monthlyCost()} kr/månad - Betalas via autogiro"
        attachNavigationOnClick(profile_payment_row, "payment")
    }

    private fun attachNavigationOnClick(view: View, subscreen: String) {
        view.setOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", subscreen)
            localBroadcastManager.sendBroadcast(intent)
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
