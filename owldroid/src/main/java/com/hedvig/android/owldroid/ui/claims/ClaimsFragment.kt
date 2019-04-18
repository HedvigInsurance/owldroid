package com.hedvig.android.owldroid.ui.claims

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
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.ui.claims.commonclaim.CommonClaimsAdapter
import com.hedvig.android.owldroid.util.extensions.*
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
    private val baseMarginHalf: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_half) }

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.claimsNavigationHost)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        setupLargeTitle(R.string.CLAIMS_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            requireActivity().findNavController(R.id.claimsNavigationHost).popBackStack()
        }

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

                outRect.left = column * baseMarginHalf / 2
                outRect.right = baseMarginHalf - (column + 1) * baseMarginHalf / 2
                if (position >= 2) {
                    outRect.top = baseMarginHalf
                }
            }
        })
    }

    private fun setupButtons() {
        commonClaimCreateClaimButton.setOnClickListener {
            //todo open create a claim chat
        }
    }

    private fun setupCommonClaims(commonClaimsData: CommonClaimQuery.Data) {
        loadingSpinner.remove()
        commonClaimsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        commonClaimsRecyclerView.adapter = CommonClaimsAdapter(
            commonClaims = commonClaimsData.commonClaims(),
            baseUrl = baseUrl,
            requestBuilder = requestBuilder,
            navigateToCommonClaimFragment = { titleAndBulletPoint ->
                claimsViewModel.setCommonClaimByTitle(titleAndBulletPoint)
                navController.navigate(R.id.action_claimsFragment_to_commonClaimsFragment)
            },
            navigateToEmergencyFragment = { emergency ->
                claimsViewModel.setEmergencyData(emergency)
                navController.navigate(R.id.action_claimsFragment_to_emergencyFragment)
            }
        )
    }

    private fun handleNoQuickActions() {
        //TODO: UI
        Timber.i("No claims quick actions found")
    }
}
