package com.hedvig.android.owldroid.feature.claims.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.RequestBuilder
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.feature.claims.ui.commonclaim.CommonClaimsAdapter
import com.hedvig.android.owldroid.feature.claims.ui.pledge.HonestyPledgeBottomSheet
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.extensions.view.setHapticClickListener
import com.hedvig.android.owldroid.util.extensions.view.show
import com.hedvig.android.owldroid.util.svg.buildRequestBuilder
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_claims.*
import kotlinx.android.synthetic.main.loading_spinner.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ClaimsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    @field:Named("BASE_URL")
    lateinit var baseUrl: String
    private val requestBuilder: RequestBuilder<PictureDrawable> by lazy { buildRequestBuilder() }

    private lateinit var claimsViewModel: ClaimsViewModel
    private val baseMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin) }

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.rootNavigationHost)
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_claims, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.CLAIMS_TITLE, R.font.circular_bold)
        appBarLayout.setExpanded(true)

        claimsViewModel.apply {
            loadingSpinner.show()
            fetchCommonClaims()
            data.observe(this@ClaimsFragment, Observer { commonClaimsData ->
                commonClaimsData?.let { setupCommonClaims(it) } ?: handleNoQuickActions()
            })
        }
        setupButtons()

        commonClaimsRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                val column = position % 2

                outRect.left = column * baseMargin / 2
                outRect.right = baseMargin - (column + 1) * baseMargin / 2
                if (position >= 2) {
                    outRect.top = baseMargin
                }
            }
        })
    }

    private fun setupButtons() {
        commonClaimCreateClaimButton.setHapticClickListener {
            HonestyPledgeBottomSheet
                .newInstance("main_screen", R.id.action_loggedInFragment_to_chatFragment)
                .show(requireFragmentManager(), "honestyPledge")
        }
    }

    private fun setupCommonClaims(commonClaimsData: CommonClaimQuery.Data) {
        loadingSpinner.remove()
        commonClaimsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        commonClaimsRecyclerView.adapter =
            CommonClaimsAdapter(
                commonClaims = commonClaimsData.commonClaims(),
                baseUrl = baseUrl,
                requestBuilder = requestBuilder,
                navigateToCommonClaimFragment = { commonClaim ->
                    claimsViewModel.setSelectedSubViewData(commonClaim)
                    navController.navigate(R.id.action_loggedInFragment_to_commonClaimsFragment)
                },
                navigateToEmergencyFragment = { commonClaim ->
                    claimsViewModel.setSelectedSubViewData(commonClaim)
                    navController.navigate(R.id.action_loggedInFragment_to_emergencyFragment)
                }
            )
    }

    private fun handleNoQuickActions() {
        //TODO: UI
        Timber.i("No claims quick actions found")
    }
}
