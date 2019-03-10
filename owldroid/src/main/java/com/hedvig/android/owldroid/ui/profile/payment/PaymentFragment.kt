package com.hedvig.android.owldroid.ui.profile.payment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.CustomTypefaceSpan
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.concat
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_payment.*
import java.util.Calendar
import javax.inject.Inject

const val BILLING_DAY = 27

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
        profileViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.payment_title)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", "back")
            localBroadcastManager.sendBroadcast(intent)
        }

        priceSphere.drawable.compatSetTint(requireContext().compatColor(R.color.green))
        deductibleSphere.drawable.compatSetTint(requireContext().compatColor(R.color.dark_green))

        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val day = today.get(Calendar.DAY_OF_MONTH)
        val month = (today.get(Calendar.MONTH) + 1).let { month ->
            if (day > BILLING_DAY) {
                month + 1
            } else {
                month
            }
        }.let { String.format("%02d", it) }

        autogiroDate.text = "Nästa dras $year-$month-$BILLING_DAY"

        changeBankAccount.setOnClickListener {
            openTrustly()
        }

        connectBankAccount.setOnClickListener {
            openTrustly()
        }

        loadData()
    }

    private fun openTrustly() {
        val intent = Intent("profileNavigation")
        intent.putExtra("action", "trustly")
        localBroadcastManager.sendBroadcast(intent)

    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            sphereContainer.show()

            val monthlyCost = profileData?.insurance()?.monthlyCost()?.toString()
            val amountPartOne = SpannableString("$monthlyCost\n")
            val perMonthLabel = "kr/mån"
            val amountPartTwo = SpannableString(perMonthLabel)
            amountPartTwo.setSpan(
                CustomTypefaceSpan(requireContext().compatFont(R.font.circular_book)),
                0,
                perMonthLabel.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
            amountPartTwo.setSpan(
                AbsoluteSizeSpan(20, true),
                0,
                perMonthLabel.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
            profile_payment_amount.text = amountPartOne.concat(amountPartTwo)

            val bankAccount = profileData?.bankAccount()
            if (bankAccount != null) {
                paymentDetailsContainer.show()
                separator.show()
                changeBankAccount.show()
                bankName.text = bankAccount.bankName()
                accountNumber.text = bankAccount.descriptor()
            } else {
                connectBankAccountContainer.show()
            }
        })
    }
}
