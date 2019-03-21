package com.hedvig.android.owldroid.ui.profile.coinsured

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.CustomTypefaceSpan
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.concat
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_coinsured.*
import javax.inject.Inject

class CoinsuredFragment : Fragment() {

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
        inflater.inflate(R.layout.fragment_coinsured, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.PROFILE_COINSURED_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().findNavController(R.id.profileNavigationHost).popBackStack()
        }

        coinsuredSphere.drawable.compatSetTint(requireContext().compatColor(R.color.purple))

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            sphereContainer.show()
            textContainer.show()

            loadingAnimation.show()
            loadingAnimation.useHardwareAcceleration(true)
            loadingAnimation.playAnimation()

            profileData?.insurance()?.personsInHousehold()?.let { personsInHousehold ->
                val label = resources.getString(R.string.PROFILE_COINSURED_QUANTITY_LABEL)
                val partOne = SpannableString("$personsInHousehold\n")
                val partTwo = SpannableString(label)
                partOne.setSpan(
                    CustomTypefaceSpan(requireContext().compatFont(R.font.soray_extrabold)),
                    0,
                    1,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
                partTwo.setSpan(AbsoluteSizeSpan(16, true), 0, label.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

                sphereText.text = partOne.concat(partTwo)
            }
        })
    }
}
