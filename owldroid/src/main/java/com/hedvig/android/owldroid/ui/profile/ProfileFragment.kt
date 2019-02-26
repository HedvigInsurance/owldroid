package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        observeProfile()
        return view
    }

    private fun observeProfile() {
        profileViewModel.data.observe(this, Observer { profileData ->
            profile_loading_spinner.visibility = ProgressBar.GONE
            profile_rows_container.visibility = LinearLayout.VISIBLE

            setupProfileRow(profileData!!)
            setupPaymentRow(profileData)
        })
    }

    private fun setupProfileRow(profileData: ProfileQuery.Data) {
//        val firstName = profileData.member().firstName().or("")
//        val lastName = profileData.member().lastName().or("")
//        profile_info_row_name.text = "$firstName $lastName"
        profileInfoRow.setOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("subscreen", "my_info")
            getLocalBroadcastManager().sendBroadcast(intent)
        }
    }

    private fun setupPaymentRow(profileData: ProfileQuery.Data) {
//        val cost = profileData.insurance().monthlyCost().or(0)
//        profile_payment_monthly_cost.text = "$cost kr/månad · Betalas via autogiro"
//        profile_payment_row.setOnClickListener {
//            val intent = Intent("profileNavigation")
//            intent.putExtra("subscreen", "payment")
//            getLocalBroadcastManager().sendBroadcast(intent)
//        }
    }


    private fun getLocalBroadcastManager() = LocalBroadcastManager.getInstance(context!!)
}
