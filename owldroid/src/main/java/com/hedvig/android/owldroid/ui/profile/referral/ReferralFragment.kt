package com.hedvig.android.owldroid.ui.profile.referral

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
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.service.RemoteConfig
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import com.hedvig.android.owldroid.util.extensions.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_referral.*
import timber.log.Timber
import javax.inject.Inject

class ReferralFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var remoteConfig: RemoteConfig

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
        inflater.inflate(R.layout.fragment_referral, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collapsingToolbar.title = resources.getString(R.string.PROFILE_REFERRAL_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", "back")
            localBroadcastManager.sendBroadcast(intent)
        }

        val incentive = remoteConfig.referralsIncentiveAmount.toString()

        youGetDescription.text = interpolateTextKey(
            resources.getString(R.string.PROFILE_REFERRAL_YOU_GET_DESCRIPTION),
            hashMapOf("INCENTIVE" to incentive)
        )
        theyGetDescription.text = interpolateTextKey(
            resources.getString(R.string.PROFILE_REFERRAL_THEY_GET_DESCRIPTION),
            hashMapOf("INCENTIVE" to incentive)
        )

        referralButton.background.compatSetTint(requireContext().compatColor(R.color.purple))
        termsLink.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hedvig.com/invite/terms")))
        }

        profileViewModel.data.observe(this, Observer { data ->
            data?.member()?.id()?.let { memberId ->
                FirebaseDynamicLinks
                    .getInstance()
                    .createDynamicLink()
                    .setDomainUriPrefix(remoteConfig.referralsDomain)
                    .setLink(Uri.parse("https://www.hedvig.com/referrals?memberId=$memberId&incentive=$incentive"))
                    .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                    .setIosParameters(DynamicLink.IosParameters.Builder(remoteConfig.referralsIosBundleId).build())
                    .setSocialMetaTagParameters(
                        DynamicLink.SocialMetaTagParameters.Builder()
                            .setTitle(resources.getString(R.string.PROFILE_REFERRAL_LINK_SOCIAL_TITLE))
                            .setDescription(resources.getString(R.string.PROFILE_REFERRAL_LINK_SOCIAL_DESCRIPTION))
                            .setImageUrl(Uri.parse(resources.getString(R.string.PROFILE_REFERRAL_LINK_SOCIAL_IMAGE_URL)))
                            .build()
                    )
                    .buildShortDynamicLink()
                    .addOnSuccessListener { link ->
                        referralButton.show()
                        referralButton.setOnClickListener {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    interpolateTextKey(
                                        resources.getString(R.string.PROFILE_REFERRAL_SHARE_TEXT),
                                        hashMapOf("INCENTIVE" to incentive, "LINK" to link.shortLink.toString())
                                    )
                                )
                                type = "text/plain"
                            }
                            val chooser = Intent.createChooser(shareIntent, "Dela Hedvig")
                            startActivity(chooser)
                        }
                    }.addOnFailureListener { error ->
                        Timber.e(error, "Failed to create short link")
                    }
            }
        })
    }
}
