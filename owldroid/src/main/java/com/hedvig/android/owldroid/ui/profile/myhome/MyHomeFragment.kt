package com.hedvig.android.owldroid.ui.profile.myhome

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.type.InsuranceType
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_my_home.*
import javax.inject.Inject

class MyHomeFragment : Fragment() {
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
        inflater.inflate(R.layout.fragment_my_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.PROFILE_MY_HOME_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().findNavController(R.id.loggedInNavigationHost).popBackStack()
        }

        changeHomeInformation.setOnClickListener {
            fragmentManager?.let { fm ->
                val changeHomeInformationDialog = ChangeHomeInfoDialog()
                val transaction = fm.beginTransaction()
                val prev = fm.findFragmentByTag("dialog")
                prev?.let { transaction.remove(it) }
                transaction.addToBackStack(null)
                changeHomeInformationDialog.show(transaction, "dialog")
            }
        }

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()

            profileData?.insurance()?.let { insuranceData ->
                address.text = insuranceData.address()
                postalNumber.text = insuranceData.postalNumber()
                insuranceType.text =
                    when (insuranceData.type()) {
                        InsuranceType.BRF -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_BRF)
                        InsuranceType.STUDENT_BRF -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_BRF)
                        InsuranceType.RENT -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_RENT)
                        InsuranceType.STUDENT_RENT -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_RENT)
                        else -> ""
                    }
                infoContainer.show()
            }
        })
    }
}
