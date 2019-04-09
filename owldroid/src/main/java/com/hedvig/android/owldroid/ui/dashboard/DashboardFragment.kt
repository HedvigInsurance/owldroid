package com.hedvig.android.owldroid.ui.dashboard

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import com.hedvig.android.owldroid.util.extensions.observe
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.loading_spinner.*
import javax.inject.Inject

class DashboardFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var dashboardViewModel: DashboardViewModel

    private var personalCoverageCardOpen: Boolean = false

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData() {
        val halfMargin = resources.getDimensionPixelSize(R.dimen.base_margin_half)
        val tripleMargin = resources.getDimensionPixelSize(R.dimen.base_margin_triple)

        dashboardViewModel.data.observe(this) { data ->
            loadingSpinner.remove()

            data?.let { d ->
                val title = interpolateTextKey(
                    resources.getString(R.string.DASHBOARD_TITLE),
                    "NAME" to d.member().firstName()
                )
                setupLargeTitle(title, R.font.circular_bold)
            }

            perilCategoryContainer.removeAllViews()

            data?.insurance()?.perilCategories()?.forEach { category ->
                val categoryView = PerilCategoryView(requireContext())
                categoryView.categoryIconUrl = Uri.parse(category.iconUrl())
                categoryView.layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).also { lp ->
                    lp.topMargin = halfMargin
                    lp.marginStart = tripleMargin
                    lp.marginEnd = tripleMargin
                    lp.bottomMargin = halfMargin
                }
                categoryView.title = category.title()
                categoryView.subtitle = category.description()
                perilCategoryContainer.addView(categoryView)
            }

            val additionalInformation = PerilCategoryView(requireContext())

            additionalInformation.categoryIcon = requireContext().compatDrawable(R.drawable.ic_more_info)
            additionalInformation.title = resources.getString(R.string.DASHBOARD_MORE_INFO_TITLE)
            additionalInformation.subtitle = resources.getString(R.string.DASHBOARD_MORE_INFO_SUBTITLE)

            additionalInformation.layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).also { lp ->
                lp.topMargin = halfMargin
                lp.marginStart = tripleMargin
                lp.marginEnd = tripleMargin
                lp.bottomMargin = tripleMargin
            }
            additionalInformation.expandedContent = layoutInflater.inflate(
                R.layout.dashboard_footnotes,
                additionalInformation.expandedContentContainer,
                false
            )

            perilCategoryContainer.addView(additionalInformation)
        }
    }
}
