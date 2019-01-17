package com.hedvig.android.owldroid.ui.profile.payment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.compatSetTint
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_payment.*
import javax.inject.Inject

class PaymentFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var profileViewModel: ProfileViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_payment, container, false)

        observeProfile()

        return view
    }

    private fun observeProfile() {
        profileViewModel.member.observe(this, Observer {
            profile_payment_loading_spinner.visibility = ProgressBar.GONE
            profile_payment_amount_container.visibility = RelativeLayout.VISIBLE
            profile_payment_sphere.drawable.compatSetTint(ContextCompat.getColor(context!!, R.color.green))
            profile_payment_amount.text = "179"
        })
    }

}
