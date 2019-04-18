package com.hedvig.android.owldroid.ui.profile.payment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.type.DirectDebitStatus
import com.hedvig.android.owldroid.ui.common.DirectDebitViewModel
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.CustomTypefaceSpan
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.concat
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.extensions.view.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_payment.*
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

class PaymentFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var directDebitViewModel: DirectDebitViewModel

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.loggedInNavigationHost)
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
        directDebitViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(DirectDebitViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_PAYMENT_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            navController.popBackStack()
        }

        priceSphere.drawable.compatSetTint(requireContext().compatColor(R.color.green))
        deductibleSphere.drawable.compatSetTint(requireContext().compatColor(R.color.dark_green))

        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR).toString()
        val day = today.get(Calendar.DAY_OF_MONTH)
        val month = (today.get(Calendar.MONTH) + 1).let { month ->
            if (day > BILLING_DAY) {
                month + 1
            } else {
                month
            }
        }.let { String.format("%02d", it) }

        autogiroDate.text = interpolateTextKey(
            resources.getString(R.string.PROFILE_PAYMENT_NEXT_CHARGE_DATE),
            "YEAR" to year,
            "MONTH" to month,
            "DAY" to BILLING_DAY.toString()
        )

        changeBankAccount.setOnClickListener {
            navController.navigate(R.id.action_paymentFragment_to_trustlyFragment)
        }

        connectBankAccount.setOnClickListener {
            navController.navigate(R.id.action_paymentFragment_to_trustlyFragment)
        }

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            resetViews()
            sphereContainer.show()

            val monthlyCost = profileData?.insurance()?.monthlyCost()?.toString()
            val amountPartOne = SpannableString("$monthlyCost\n")
            val perMonthLabel = resources.getString(R.string.PROFILE_PAYMENT_PER_MONTH_LABEL)
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

            bindBankAccountInformation()
        })
        directDebitViewModel.data.observe(this, Observer {
            bindBankAccountInformation()
        })
    }

    private fun resetViews() {
        connectBankAccountContainer.remove()
        changeBankAccount.remove()
        separator.remove()
        bankAccountUnderChangeParagraph.remove()
    }

    private fun bindBankAccountInformation() {
        val profileData = profileViewModel.data.value ?: return
        val directDebitStatus = directDebitViewModel.data.value?.directDebitStatus() ?: return

        when (directDebitStatus) {
            DirectDebitStatus.ACTIVE -> {
                paymentDetailsContainer.show()
                bankName.text = profileData.bankAccount()?.bankName() ?: ""

                separator.show()
                accountNumber.text = profileData.bankAccount()?.descriptor() ?: ""
                changeBankAccount.show()
            }
            DirectDebitStatus.PENDING -> {
                paymentDetailsContainer.show()
                bankName.text = profileData.bankAccount()?.bankName()

                accountNumber.text = resources.getString(R.string.PROFILE_PAYMENT_ACCOUNT_NUMBER_CHANGING)
                bankAccountUnderChangeParagraph.show()
            }
            DirectDebitStatus.NEEDS_SETUP -> connectBankAccountContainer.show()
            else -> {
                Timber.e("Payment fragment direct debit status UNKNOWN!")
            }
        }
    }

    companion object {
        const val BILLING_DAY = 27
    }
}
