package com.hedvig.android.owldroid.ui.profile.payment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.content.res.ResourcesCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.CustomTypefaceSpan
import com.hedvig.android.owldroid.util.extensions.compatSetTint
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

        loadData()

        return view
    }

    private fun loadData() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context!!)
        profileViewModel.data.observe(this, Observer { profileData ->
            profile_payment_loading_spinner.visibility = ProgressBar.GONE
            profile_payment_amount_container.visibility = RelativeLayout.VISIBLE
            profile_payment_details_container.visibility = LinearLayout.VISIBLE
            profile_payment_bank_container.visibility = LinearLayout.VISIBLE
            profile_payment_change_bank_account.visibility = Button.VISIBLE
            profile_payment_change_bank_account.background.compatSetTint(ContextCompat.getColor(context!!, R.color.dark_purple))

            profile_payment_price_sphere.drawable.compatSetTint(ContextCompat.getColor(context!!, R.color.green))
            val monthlyCost = profileData!!.insurance().monthlyCost()?.toString()
            val amountPartOne = SpannableString("$monthlyCost\n")
            val perMonthLabel = "kr/mån"
            val amountPartTwo = SpannableString(perMonthLabel)
            amountPartTwo.setSpan(CustomTypefaceSpan(ResourcesCompat.getFont(context!!, R.font.circular_book)), 0, perMonthLabel.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            amountPartTwo.setSpan(AbsoluteSizeSpan(20, true), 0, perMonthLabel.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            profile_payment_amount.text = TextUtils.concat(amountPartOne, amountPartTwo)

            val bankAccount = profileData.bankAccount()
            if (bankAccount != null) {
                profile_payment_bank.text = bankAccount.bankName()
                profile_payment_account.text = bankAccount.descriptor()
            }

            profile_payment_change_bank_account.setOnClickListener {
                val intent = Intent("profileNavigation")
                intent.putExtra("subscreen", "trustly")
                localBroadcastManager.sendBroadcast(intent)
            }
        })
    }
}
