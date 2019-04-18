package com.hedvig.android.owldroid.ui.claims.commonclaim

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.ui.claims.ClaimsViewModel
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.mapppedColor
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.fragment_emergency.*
import javax.inject.Inject
import com.bumptech.glide.RequestBuilder
import com.hedvig.android.owldroid.util.svg.buildRequestBuilder
import javax.inject.Named
import android.content.Intent
import com.hedvig.android.owldroid.util.extensions.makeACall

class EmergencyFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    @field:Named("BASE_URL")
    lateinit var baseUrl: String
    private val requestBuilder: RequestBuilder<PictureDrawable> by lazy { buildRequestBuilder() }

    private lateinit var claimsViewModel: ClaimsViewModel

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.claimsNavigationHost)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_emergency, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        claimsViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ClaimsViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        claimsViewModel.emergencyData.observe(this, Observer { emergency ->
            emergency?.let { bindData(it) }
        })
    }

    private fun bindData(data: CommonClaimQuery.CommonClaim) {
        (data.layout() as CommonClaimQuery.AsEmergency).let { layout ->
            val backgroundColor = requireContext().compatColor(layout.color().mapppedColor())
            setupLargeTitle(data.title(), R.font.circular_bold, R.drawable.ic_back, backgroundColor) {
                navController.popBackStack()
            }
            commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)
        }

        appBarLayout.setExpanded(false, false)

        requestBuilder.load(Uri.parse(baseUrl + data.icon().svgUrl())).into(commonClaimFirstMessageIcon)
        commonClaimFirstMessage.text = getString(R.string.CLAIMS_EMERGENCY_FIRST_MESSAGE)
        commonClaimCreateClaimButton.remove()

        firstEmergencyButton.setOnClickListener {
            // todo: Hedvig call me
        }
        secondEmergencyButton.setOnClickListener {
            makeACall(GLOBAL_ASSISTANCE_URI)
        }
        thirdEmergencyButton.setOnClickListener {
            // todo: Write to hedvig
        }
    }

    companion object {
        private val GLOBAL_ASSISTANCE_URI = Uri.parse("tel:0123456789") // todo actual global assistance number
    }
}


