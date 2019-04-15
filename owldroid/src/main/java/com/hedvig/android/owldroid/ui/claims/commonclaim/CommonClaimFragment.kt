package com.hedvig.android.owldroid.ui.claims.commonclaim

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.ui.claims.ClaimsViewModel
import com.hedvig.android.owldroid.ui.claims.commonclaim.bulletpoint.BulletPointsAdapter
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.mapppedColor
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.fragment_common_claim.*
import javax.inject.Inject

class CommonClaimFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var claimsViewModel: ClaimsViewModel

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.claimsNavigationHost)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_common_claim, container, false)

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
        claimsViewModel.titleAndBulletPointData.observe(this, Observer { titleAndBulletPoint ->
            titleAndBulletPoint?.let { bindData(it) } // todo ?: handleNoQuickActions()
        })
    }

    private fun bindData(data: CommonClaimQuery.AsTitleAndBulletPoints) {
        val backgroundColor = requireContext().compatColor(data.color().mapppedColor())
        setupLargeTitle(data.title(), R.font.circular_bold, R.drawable.ic_back, backgroundColor) {
            navController.popBackStack()
        }
        appBarLayout.setExpanded(false, false)

        commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)
        //todo change
        commonClaimCellIcon.setImageDrawable(requireContext().compatDrawable(R.drawable.icon_charity))
        commonClaimFirstMessage.text = data.claimFirstMessage()
        commonClaimCreateClaimButton.text = data.buttonTitle()
        commonClaimCreateClaimButton.setOnClickListener {
            //todo
        }

        bulletPointsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bulletPointsRecyclerView.adapter = BulletPointsAdapter(data.bulletPoints())
    }
}
