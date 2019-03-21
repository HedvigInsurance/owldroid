package com.hedvig.android.owldroid.ui.profile.myinfo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.ActionMenuItemView
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.onChange
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import com.hedvig.android.owldroid.util.validateEmail
import com.hedvig.android.owldroid.util.validatePhoneNumber
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_my_info.*
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
        setHasOptionsMenu(true)
        profileViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_my_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        collapsingToolbar.title = resources.getString(R.string.PROFILE_MY_INFO_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().findNavController(R.id.profileNavigationHost).popBackStack()
        }
        sphere.drawable.compatSetTint(requireContext().compatColor(R.color.dark_purple))

        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val dirty = profileViewModel.dirty.value
        if (dirty == null || !dirty) {
            menu.removeItem(R.id.save)
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val prevEmail = profileViewModel.data.value?.member()?.email()
        val prevPhoneNumber = profileViewModel.data.value?.member()?.phoneNumber()

        val newEmail = emailInput.text.toString()
        val newPhoneNumber = phoneNumberInput.text.toString()

        if (prevEmail != newEmail && !validateEmail(newEmail).isSuccessful) {
            provideValidationNegativeHapticFeedback()
            fragmentManager?.let { fm ->
                val dialog =
                    ValidationDialog.newInstance(
                        R.string.PROFILE_MY_INFO_VALIDATION_DIALOG_TITLE,
                        R.string.PROFILE_MY_INFO_VALIDATION_DIALOG_DESCRIPTION_EMAIL,
                        R.string.PROFILE_MY_INFO_VALIDATION_DIALOG_DISMISS
                    )
                val transaction = fm.beginTransaction()
                val prev = fm.findFragmentByTag("validation")
                prev?.let { transaction.remove(it) }
                transaction.addToBackStack(null)
                dialog.show(transaction, "validation")
            }
            return true
        }

        if (prevPhoneNumber != newPhoneNumber && !validatePhoneNumber(newPhoneNumber).isSuccessful) {
            provideValidationNegativeHapticFeedback()
            fragmentManager?.let { fm ->
                val dialog = ValidationDialog.newInstance(
                    R.string.PROFILE_MY_INFO_VALIDATION_DIALOG_TITLE,
                    R.string.PROFILE_MY_INFO_VALIDATION_DIALOG_DESCRIPTION_PHONE_NUMBER,
                    R.string.PROFILE_MY_INFO_VALIDATION_DIALOG_DISMISS
                )
                val transaction = fm.beginTransaction()
                val prev = fm.findFragmentByTag("validation")
                prev?.let { transaction.remove(it) }
                transaction.addToBackStack(null)
                dialog.show(transaction, "validation")
            }
            return true
        }

        profileViewModel.saveInputs(emailInput.text.toString(), phoneNumberInput.text.toString())
        return true
    }

    private fun provideValidationNegativeHapticFeedback() =
        view?.findViewById<ActionMenuItemView>(R.id.save)?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            sphereContainer.show()

            contactDetailsContainer.show()

            profileData?.let { data ->
                name.text = resources.getString(
                    R.string.first_last_name,
                    data.member().firstName(),
                    data.member().lastName()
                )
                setupEmailInput(data.member().email() ?: "")
                setupPhoneNumberInput(data.member().phoneNumber() ?: "")
            }

            profileViewModel.dirty.observe(this, Observer {
                activity?.invalidateOptionsMenu()
            })
        })
    }

    private fun setupEmailInput(prefilledEmail: String) {
        emailInput.setText(prefilledEmail)

        emailInput.onChange { value ->
            profileViewModel.emailChanged(value)
            if (emailInputContainer.isErrorEnabled) {
                val validationResult = validateEmail(value)
                if (validationResult.isSuccessful) {
                    emailInputContainer.isErrorEnabled = false
                }
            }
        }

        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                return@setOnFocusChangeListener
            }

            val validationResult = validateEmail(emailInput.text.toString())
            if (!validationResult.isSuccessful) {
                emailInputContainer.error = requireContext().getString(validationResult.errorTextKey!!)
            }
        }
    }

    private fun setupPhoneNumberInput(prefilledPhoneNumber: String) {
        phoneNumberInput.setText(prefilledPhoneNumber)

        phoneNumberInput.onChange { value ->
            profileViewModel.phoneNumberChanged(value)
            if (phoneNumberInputContainer.isErrorEnabled) {
                val validationResult = validatePhoneNumber(value)
                if (validationResult.isSuccessful) {
                    phoneNumberInputContainer.isErrorEnabled = false
                }
            }
        }

        phoneNumberInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                return@setOnFocusChangeListener
            }
            val validationResult = validatePhoneNumber(phoneNumberInput.text.toString())
            if (!validationResult.isSuccessful) {

                phoneNumberInputContainer.error = requireContext().getString(validationResult.errorTextKey!!)
            }
        }
    }
}
