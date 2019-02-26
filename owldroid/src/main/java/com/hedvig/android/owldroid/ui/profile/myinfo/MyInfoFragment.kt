package com.hedvig.android.owldroid.ui.profile.myinfo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.compatSetTint
import com.hedvig.android.owldroid.util.remove
import com.hedvig.android.owldroid.util.show
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_my_info.*
import javax.inject.Inject

class MyInfoFragment : Fragment() {

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
        val view = inflater.inflate(
                R.layout.activity_my_info,
                container,
                false
        )

        loadData()

        return view
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            profile_my_info_loading_spinner.remove()
            profile_my_info_name_container.show()

            profile_my_info_sphere.drawable.compatSetTint(ContextCompat.getColor(context!!, R.color.dark_purple))
            profile_my_info_contact_details_container.show()

            profile_my_info_name.text = "${profileData!!.member().firstName().get()}\n${profileData.member().lastName().get()}"
            profile_my_info_email.text = profileData.member().email().or("")
            profile_my_info_phone_number.text = profileData.member().phoneNumber().or("")
        })
    }
}
