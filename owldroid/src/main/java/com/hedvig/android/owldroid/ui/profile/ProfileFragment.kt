package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.ProfileQuery
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_profile.*
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
        profileViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        } ?: throw Exception("No Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        populateData()
        return view
    }

    private fun populateData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            profile_loading_spinner.visibility = ProgressBar.GONE
            profile_rows_container.visibility = LinearLayout.VISIBLE

            setupMyInfoRow(profileData!!)
            setupMyHomeRow(profileData)
            setupCoinsured(profileData)
            setupCharity(profileData)
            setupPayment(profileData)
        })
    }

    private fun setupMyInfoRow(profileData: ProfileQuery.Data) {
        val firstName = profileData.member().firstName().or("")
        val lastName = profileData.member().lastName().or("")
        profile_my_info_row.description = "$firstName $lastName"
        attachNavigationOnClick(profile_my_info_row, "my_info")
    }

    private fun setupMyHomeRow(profileData: ProfileQuery.Data) {
        profile_my_home_row.description = profileData.insurance().address().get()
        attachNavigationOnClick(profile_my_home_row, "my_home")
    }

    private fun setupCoinsured(profileData: ProfileQuery.Data) {
        val personsInHousehold = profileData.insurance().personsInHousehold().get()
        profile_coinsured_row.description = "${personsInHousehold - 1} medförsäkrade"
        attachNavigationOnClick(profile_coinsured_row, "coinsured")
    }

    private fun setupCharity(profileData: ProfileQuery.Data) {
        profile_charity_row.description = profileData.cashback().name().get()
        attachNavigationOnClick(profile_charity_row, "charity")
    }

    private fun setupPayment(profileData: ProfileQuery.Data) {
        profile_payment_row.description = profileData.insurance().monthlyCost().get().toString() + "kr/månad - Betalas via autogiro"
        attachNavigationOnClick(profile_payment_row, "payment")
    }

    private fun attachNavigationOnClick(view: View, subscreen: String) {
        view.setOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("subscreen", subscreen)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        }
    }
}
