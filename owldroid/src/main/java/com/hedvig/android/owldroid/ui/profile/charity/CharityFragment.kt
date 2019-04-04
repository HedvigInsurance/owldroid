package com.hedvig.android.owldroid.ui.profile.charity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.ui.common.RoundedBottomSheetDialogFragment
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.show
import com.hedvig.android.owldroid.util.extensions.showBottomSheetDialog
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_charity.*
import kotlinx.android.synthetic.main.loading_spinner.*
import javax.inject.Inject

class CharityFragment : Fragment() {

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
        inflater.inflate(R.layout.fragment_charity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_CHARITY_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            requireActivity().findNavController(R.id.profileNavigationHost).popBackStack()
        }

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()

            profileData?.let { data ->
                data.cashback()?.let { showSelectedCharity(it) } ?: showCharityPicker(data.cashbackOptions())
            }
        })
    }

    private fun showSelectedCharity(cashback: ProfileQuery.Cashback) {
        selectedCharityContainer.show()
        selectCharityContainer.remove()

        Glide
            .with(requireContext())
            .load(cashback.imageUrl())
            .apply(RequestOptions().override(Target.SIZE_ORIGINAL, CASH_BACK_IMAGE_HEIGHT))
            .into(selectedCharityBanner)

        selectedCharityCardTitle.text = cashback.name()
        selectedCharityCardParagraph.text = cashback.paragraph()
        charitySelectedHowDoseItWorkButton.setOnClickListener {
            requireFragmentManager().showBottomSheetDialog(R.layout.bottom_sheet_charity_explanation)
        }
    }

    private fun showCharityPicker(options: List<ProfileQuery.CashbackOption>) {
        selectCharityContainer.show()
        cashbackOptions.layoutManager = LinearLayoutManager(requireContext())
        cashbackOptions.adapter = CharityAdapter(options, requireContext()) { id ->
            profileViewModel.selectCashback(id)
        }
        selectCharityHowDoesItWorkButton.setOnClickListener {
            requireFragmentManager().showBottomSheetDialog(R.layout.bottom_sheet_charity_explanation)
        }
    }

    companion object {
        private const val CASH_BACK_IMAGE_HEIGHT = 200
    }
}
