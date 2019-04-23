package com.hedvig.android.owldroid.ui.claims.commonclaim

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.ui.claims.ClaimsViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.common_claim_first_message.*
import javax.inject.Inject
import com.bumptech.glide.RequestBuilder
import com.hedvig.android.owldroid.util.extensions.observe
import com.hedvig.android.owldroid.util.svg.buildRequestBuilder
import javax.inject.Named

abstract class BaseCommonClaimFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    @field:Named("BASE_URL")
    lateinit var baseUrl: String
    val requestBuilder: RequestBuilder<PictureDrawable> by lazy { buildRequestBuilder() }

    lateinit var claimsViewModel: ClaimsViewModel

    val navController: NavController by lazy {
        requireActivity().findNavController(R.id.loggedInNavigationHost)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        claimsViewModel.selectedSubViewData.observe(this) { commonClaim ->
            commonClaim?.let { bindData(it) }
        }
    }

    @CallSuper
    open fun bindData(data: CommonClaimQuery.CommonClaim) {
        appBarLayout.setExpanded(false, false)
        requestBuilder.load(Uri.parse(baseUrl + data.icon().svgUrl())).into(commonClaimFirstMessageIcon)
    }
}


