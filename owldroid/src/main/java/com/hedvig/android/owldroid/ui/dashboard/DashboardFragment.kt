package com.hedvig.android.owldroid.ui.dashboard

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.DashboardQuery
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.android.owldroid.util.extensions.addViews
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import com.hedvig.android.owldroid.util.extensions.observe
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.showBottomSheetDialog
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.extensions.view.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.loading_spinner.*
import javax.inject.Inject

class DashboardFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var dashboardViewModel: DashboardViewModel

    private val halfMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_half) }
    private val doubleMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_double) }
    private val tripleMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_triple) }
    private val perilTotalWidth: Int by lazy { resources.getDimensionPixelSize(R.dimen.peril_width) + doubleMargin * 2 }
    private val rowWidth: Int by lazy { dashboardParent.measuredWidth - doubleMargin * 2 }

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
            data?.chatActions()?.let { setupActionMenu(it) }

            data?.insurance()?.perilCategories()?.forEach { category ->
                val categoryView = makePerilCategoryRow(category)
                perilCategoryContainer.addView(categoryView)
            }

            data?.insurance()?.status()?.let { insuranceStatus ->
                when (insuranceStatus) {
                    InsuranceStatus.ACTIVE -> insuranceActive.show()
                    else -> {
                    }
                }
            }

            setupAdditionalInformationRow()
        }
    }

    private fun setupActionMenu(actions: List<DashboardQuery.ChatAction>) {
        actionMenuTitle.show()

        actionContainer.isNestedScrollingEnabled = false
        actionContainer.layoutManager =
            LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL }
        actionContainer.adapter = ActionAdapter(actions, requireContext())
        actionContainer.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.apply {
                    left = if (parent.getChildAdapterPosition(view) == 0) doubleMargin else halfMargin

                    parent.adapter?.itemCount?.let { count ->
                        right = if (parent.getChildAdapterPosition(view) == count - 1) doubleMargin else halfMargin
                    }

                }
            }
        })

        actionContainer.show()
    }

    private fun makePerilCategoryRow(category: DashboardQuery.PerilCategory): PerilCategoryView {
        val categoryView = PerilCategoryView(requireContext())
        categoryView.categoryIconId = category.iconUrl()
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
        categoryView.expandedContentContainer.measure(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        category.perils()?.let { categoryView.expandedContent = makePerilCategoryExpandContent(it) }

        return categoryView
    }

    private fun makePerilCategoryExpandContent(perils: List<DashboardQuery.Peril>): LinearLayout {
        val expandedContent = LinearLayout(requireContext())

        val maxPerilsPerRow = calculateMaxPerilsPerRow()
        if (perils.size > maxPerilsPerRow) {
            expandedContent.orientation = LinearLayout.VERTICAL
            val firstRowPerils = perils.take(maxPerilsPerRow)
            val lastRowPerils = perils.drop(maxPerilsPerRow)

            val firstRow = LinearLayout(requireContext())
            firstRow.addViews(firstRowPerils.map { makePeril(it) })

            val lastRow = LinearLayout(requireContext())
            lastRow.addViews(lastRowPerils.map { makePeril(it) })

            expandedContent.addViews(firstRow, lastRow)
        } else {
            expandedContent.addViews(perils.map { makePeril(it) })
        }


        return expandedContent
    }

    private fun calculateMaxPerilsPerRow(): Int {
        var counter = 0
        var accumulator = 0
        while (true) {
            if (accumulator + perilTotalWidth > rowWidth) {
                break
            }
            accumulator += perilTotalWidth
            counter += 1
        }

        return counter
    }

    private fun makePeril(peril: DashboardQuery.Peril): PerilView {
        val perilView = PerilView(requireContext())
        perilView.layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also { lp ->
            lp.topMargin = doubleMargin
            lp.marginStart = doubleMargin
            lp.marginEnd = doubleMargin
        }

        perilView.perilName = peril.title()
        peril.id()?.let { perilView.perilIconId = it }
        perilView.setOnClickListener {
            //requireFragmentManager().showBottomSheetDialog()
        }

        return perilView
    }

    private fun setupAdditionalInformationRow() {
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
