package com.hedvig.android.owldroid.feature.profile.ui.referral

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.feature.profile.service.ProfileTracker
import com.hedvig.android.owldroid.feature.profile.ui.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.observe
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.increaseTouchableArea
import com.hedvig.android.owldroid.util.extensions.view.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_referral.*
import javax.inject.Inject

class ReferralFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var tracker: ProfileTracker

    private lateinit var profileViewModel: ProfileViewModel

    private var buttonAnimator: ValueAnimator? = null

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
        inflater.inflate(R.layout.fragment_referral, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_REFERRAL_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            requireActivity().findNavController(R.id.rootNavigationHost).popBackStack()
        }
        referralButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            requireContext().compatDrawable(R.drawable.icon_share_white),
            null
        )

        profileViewModel.remoteConfigData.observe(this) { remoteConfigData ->
            remoteConfigData?.let { rcd ->
                val incentive = rcd.referralsIncentiveAmount.toString()

                youGetDescription.text = interpolateTextKey(
                    resources.getString(R.string.PROFILE_REFERRAL_YOU_GET_DESCRIPTION),
                    "INCENTIVE" to incentive
                )
                theyGetDescription.text = interpolateTextKey(
                    resources.getString(R.string.PROFILE_REFERRAL_THEY_GET_DESCRIPTION),
                    "INCENTIVE" to incentive
                )

                referralButton.background.compatSetTint(requireContext().compatColor(R.color.purple))

                termsLink.increaseTouchableArea(100)
                termsLink.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hedvig.com/invite/terms")))
                }

                profileViewModel.data.observe(this, Observer { data ->
                    data?.member()?.id()?.let { memberId ->
                        profileViewModel.generateReferralLink(memberId)
                        profileViewModel.firebaseLink.observe(this, Observer { link ->
                            referralButton.show()
                            if (referralButton.translationY != 0f) {
                                buttonAnimator = ValueAnimator.ofFloat(75f, 0f).apply {
                                    duration = 500
                                    addUpdateListener { translation ->
                                        referralButton.translationY = translation.animatedValue as Float
                                    }
                                    interpolator = OvershootInterpolator()
                                    start()
                                }
                            }
                            referralButton.setOnClickListener {
                                tracker.clickReferral(profileViewModel.remoteConfigData.value?.referralsIncentiveAmount)
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        interpolateTextKey(
                                            resources.getString(R.string.PROFILE_REFERRAL_SHARE_TEXT),
                                            "INCENTIVE" to incentive, "LINK" to link.toString()
                                        )
                                    )
                                    type = "text/plain"
                                }
                                val chooser = Intent.createChooser(
                                    shareIntent,
                                    resources.getString(R.string.PROFILE_REFERRAL_SHARE_TITLE)
                                )
                                startActivity(chooser)
                            }
                        })
                    }
                })
            }
        }
    }

    override fun onStop() {
        super.onStop()
        buttonAnimator?.removeAllListeners()
        buttonAnimator?.cancel()
    }
}
