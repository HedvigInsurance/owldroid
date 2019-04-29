package com.hedvig.android.owldroid.ui.claims.commonclaim

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.makeACall
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.performOnTapHapticFeedback
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.mapppedColor
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.fragment_emergency.*

class EmergencyFragment : BaseCommonClaimFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_emergency, container, false)

    override fun bindData(data: CommonClaimQuery.CommonClaim) {
        super.bindData(data)
        val layout = data.layout() as? CommonClaimQuery.AsEmergency ?: return
        val backgroundColor = requireContext().compatColor(layout.color().mapppedColor())
        setupLargeTitle(data.title(), R.font.circular_bold, R.drawable.ic_back, backgroundColor) {
            navController.popBackStack()
        }
        commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)

        commonClaimFirstMessage.text = getString(R.string.CLAIMS_EMERGENCY_FIRST_MESSAGE)
        commonClaimCreateClaimButton.remove()

        firstEmergencyButton.setOnClickListener {
            firstEmergencyButton.performOnTapHapticFeedback()
            claimsViewModel.triggerClaimsChat {
                navController.navigate(R.id.action_claimsEmergencyFragment_to_chatFragment)
            }
        }
        secondEmergencyButton.setOnClickListener {
            secondEmergencyButton.performOnTapHapticFeedback()
            makeACall(GLOBAL_ASSISTANCE_URI)
        }
        thirdEmergencyButton.setOnClickListener {
            thirdEmergencyButton.performOnTapHapticFeedback()
            claimsViewModel.triggerFreeTextChat {
                navController.navigate(R.id.action_claimsEmergencyFragment_to_chatFragment)
            }
        }
    }

    companion object {
        private val GLOBAL_ASSISTANCE_URI = Uri.parse("tel:+4538489461")
    }
}


