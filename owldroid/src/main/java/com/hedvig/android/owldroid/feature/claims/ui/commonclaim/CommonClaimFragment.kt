package com.hedvig.android.owldroid.feature.claims.ui.commonclaim

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.feature.claims.ui.commonclaim.bulletpoint.BulletPointsAdapter
import com.hedvig.android.owldroid.feature.claims.ui.pledge.HonestyPledgeBottomSheet
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.setHapticClickListener
import com.hedvig.android.owldroid.util.mapppedColor
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.fragment_common_claim.*

class CommonClaimFragment : BaseCommonClaimFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_common_claim, container, false)

    override fun bindData(data: CommonClaimQuery.CommonClaim) {
        super.bindData(data)
        val layout = data.layout() as? CommonClaimQuery.AsTitleAndBulletPoints ?: return
        val backgroundColor = requireContext().compatColor(layout.color().mapppedColor())
        setupLargeTitle(data.title(), R.font.circular_bold, R.drawable.ic_back, backgroundColor) {
            navController.popBackStack()
        }

        commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)

        commonClaimFirstMessage.text = layout.claimFirstMessage()
        commonClaimCreateClaimButton.text = layout.buttonTitle()
        commonClaimCreateClaimButton.setHapticClickListener {
            HonestyPledgeBottomSheet
                .newInstance(data.title(), R.id.action_claimsCommonClaimFragment_to_chatFragment)
                .show(requireFragmentManager(), "honestyPledge")
        }

        bulletPointsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bulletPointsRecyclerView.adapter =
            BulletPointsAdapter(
                layout.bulletPoints(),
                baseUrl,
                requestBuilder
            )
    }
}
