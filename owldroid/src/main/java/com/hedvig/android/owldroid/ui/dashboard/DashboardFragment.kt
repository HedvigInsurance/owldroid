package com.hedvig.android.owldroid.ui.dashboard

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.DashboardQuery
import com.hedvig.android.owldroid.type.DirectDebitStatus
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.android.owldroid.type.InsuranceType
import com.hedvig.android.owldroid.ui.common.DirectDebitViewModel
import com.hedvig.android.owldroid.util.extensions.addViews
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import com.hedvig.android.owldroid.util.extensions.observe
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.animateCollapse
import com.hedvig.android.owldroid.util.extensions.view.animateExpand
import com.hedvig.android.owldroid.util.extensions.view.performOnTapHapticFeedback
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.extensions.view.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import com.hedvig.android.owldroid.util.isApartmentOwner
import com.hedvig.android.owldroid.util.isStudentInsurance
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dashboard_footnotes.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.loading_spinner.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DashboardFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var dashboardViewModel: DashboardViewModel
    lateinit var directDebitViewModel: DirectDebitViewModel

    private val halfMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_half) }
    private val doubleMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_double) }
    private val tripleMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_triple) }
    private val perilTotalWidth: Int by lazy { resources.getDimensionPixelSize(R.dimen.peril_width) + doubleMargin * 2 }
    private val rowWidth: Int by lazy { dashboardParent.measuredWidth - doubleMargin * 2 }

    private var isInsurancePendingExplanationExpanded = false

    private var setActivationFiguresInterval: Disposable? = null
    private val compositeDisposable = CompositeDisposable()

    private val navController by lazy { requireActivity().findNavController(R.id.rootNavigationHost) }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel::class.java)
        }
        directDebitViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(DirectDebitViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onResume() {
        super.onResume()
        dashboardViewModel.data.value?.insurance()?.activeFrom()?.let { localDate ->
            setActivationFigures(localDate)
        }
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData() {
        dashboardViewModel.data.observe(this) { bindData() }
        directDebitViewModel.data.observe(this) { bindData() }
    }

    private fun bindData() {
        val dashboardData = dashboardViewModel.data.value ?: return
        val directDebitData = directDebitViewModel.data.value ?: return
        loadingSpinner.remove()
        val title = interpolateTextKey(
            resources.getString(R.string.DASHBOARD_TITLE),
            "NAME" to dashboardData.member().firstName()
        )
        setupLargeTitle(title, R.font.circular_bold)
        setupInsuranceStatusStatus(dashboardData.insurance())

        perilCategoryContainer.removeAllViews()
        dashboardData.chatActions()?.let { setupActionMenu(it) }

        dashboardData.insurance().perilCategories()?.forEach { category ->
            val categoryView = makePerilCategoryRow(category)
            perilCategoryContainer.addView(categoryView)
        }

        dashboardData.insurance().status().let { insuranceStatus ->
            when (insuranceStatus) {
                InsuranceStatus.ACTIVE -> insuranceActive.show()
                else -> {
                }
            }
        }

        dashboardData.insurance().type()?.let { setupAdditionalInformationRow(it) }

        setupDirectDebitStatus(directDebitData.directDebitStatus())
    }

    private fun setupActionMenu(actions: List<DashboardQuery.ChatAction>) {
        actionMenuTitle.show()

        actionContainer.isNestedScrollingEnabled = false
        actionContainer.layoutManager =
            LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL }
        actionContainer.adapter = ActionAdapter(actions, requireContext(), requireActivity(), dashboardViewModel)
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
        val categoryView = PerilCategoryView.build(requireContext())

        categoryView.categoryIconId = category.iconUrl()
        categoryView.title = category.title()
        categoryView.subtitle = category.description()
        categoryView.expandedContentContainer.measure(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        category.perils()?.let { categoryView.expandedContent = makePerilCategoryExpandContent(it, category) }

        return categoryView
    }

    private fun makePerilCategoryExpandContent(
        perils: List<DashboardQuery.Peril>,
        category: DashboardQuery.PerilCategory
    ): LinearLayout {
        val expandedContent = LinearLayout(requireContext())

        val maxPerilsPerRow = calculateMaxPerilsPerRow()
        if (perils.size > maxPerilsPerRow) {
            expandedContent.orientation = LinearLayout.VERTICAL
            val firstRowPerils = perils.take(maxPerilsPerRow)
            val lastRowPerils = perils.drop(maxPerilsPerRow)

            val firstRow = LinearLayout(requireContext())
            firstRow.addViews(firstRowPerils.map { makePeril(it, category) })

            val lastRow = LinearLayout(requireContext())
            lastRow.addViews(lastRowPerils.map { makePeril(it, category) })

            expandedContent.addViews(firstRow, lastRow)
        } else {
            expandedContent.addViews(perils.map { makePeril(it, category) })
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

    private fun makePeril(peril: DashboardQuery.Peril, subject: DashboardQuery.PerilCategory): PerilView {
        val perilView = PerilView.build(requireContext())

        perilView.perilName = peril.title()
        peril.id()?.let { perilView.perilIconId = it }
        perilView.setOnClickListener {
            perilView.performOnTapHapticFeedback()
            val subjectName = subject.title()
            val id = peril.id()
            val title = peril.title()
            val description = peril.description()

            if (subjectName != null && id != null && title != null && description != null) {
                PerilBottomSheet.newInstance(
                    subjectName,
                    PerilIcon.from(id),
                    title,
                    description
                ).show(requireFragmentManager(), "perilSheet")
            }
        }

        return perilView
    }

    private fun setupAdditionalInformationRow(insuranceType: InsuranceType) {
        val additionalInformation = PerilCategoryView.build(
            requireContext(),
            bottomMargin = tripleMargin
        )

        additionalInformation.categoryIcon = requireContext().compatDrawable(R.drawable.ic_more_info)
        additionalInformation.title = resources.getString(R.string.DASHBOARD_MORE_INFO_TITLE)
        additionalInformation.subtitle = resources.getString(R.string.DASHBOARD_MORE_INFO_SUBTITLE)

        val content = layoutInflater.inflate(
            R.layout.dashboard_footnotes,
            additionalInformation.expandedContentContainer,
            false
        )

        content.totalCoverageFootnote.text = interpolateTextKey(
            resources.getString(R.string.DASHBOARD_INSURANCE_AMOUNT_FOOTNOTE),
            "AMOUNT" to
                if (isStudentInsurance(insuranceType)) resources.getString(R.string.two_hundred_thousand)
                else resources.getString(R.string.one_million)
        )
        if (isApartmentOwner(insuranceType)) {
            content.ownerFootnote.show()
        }

        additionalInformation.expandedContent = content

        perilCategoryContainer.addView(additionalInformation)
    }

    private fun setupDirectDebitStatus(directDebitStatus: DirectDebitStatus) {
        when (directDebitStatus) {
            DirectDebitStatus.ACTIVE,
            DirectDebitStatus.PENDING,
            DirectDebitStatus.`$UNKNOWN` -> {
                directDebitNeedsSetup.remove()
            }
            DirectDebitStatus.NEEDS_SETUP -> {
                directDebitNeedsSetup.show()
                directDebitConnectButton.setOnClickListener {
                    navController.navigate(R.id.action_dashboardFragment_to_trustlyFragment)
                }
            }
        }
    }

    private fun setupInsuranceStatusStatus(insurance: DashboardQuery.Insurance) {
        insurancePending.remove()
        insuranceActive.remove()
        when (insurance.status()) {
            InsuranceStatus.ACTIVE -> {
                insuranceActive.show()
            }
            InsuranceStatus.INACTIVE -> {
                insurancePending.show()
                insurancePendingCountDownContainer.remove()
                insurancePendingLoadingAnimation.show()

                insurancePendingLoadingAnimation.playAnimation()
                insurancePendingExplanation.text =
                    getString(R.string.DASHBOARD_DIRECT_DEBIT_STATUS_PENDING_NO_START_DATE_EXPLANATION)
                setupInsurancePendingMoreInfo()
            }
            InsuranceStatus.INACTIVE_WITH_START_DATE -> {
                insurancePending.show()
                insurancePendingLoadingAnimation.remove()

                insurance.activeFrom()?.let { localDate ->
                    insurancePendingCountDownContainer.show()

                    setActivationFigures(localDate)
                    val formattedString = localDate.format(DateTimeFormatter.ofPattern("d LLLL yyyy"))
                    insurancePendingExplanation.text = interpolateTextKey(
                        getString(R.string.DASHBOARD_DIRECT_DEBIT_STATUS_PENDING_HAS_START_DATE_EXPLANATION),
                        "START_DATE" to formattedString
                    )
                } ?: Timber.e("InsuranceStatus INACTIVE_WITH_START_DATE but got no start date")

                setupInsurancePendingMoreInfo()
            }
            InsuranceStatus.`$UNKNOWN`,
            InsuranceStatus.PENDING,
            InsuranceStatus.TERMINATED -> {
            }
        }
    }

    private fun setupInsurancePendingMoreInfo() {
        insurancePendingMoreInfo.setOnClickListener {
            if (isInsurancePendingExplanationExpanded) {
                insurancePendingExplanation.animateCollapse()
            } else {
                insurancePendingExplanation.animateExpand()
            }
            isInsurancePendingExplanationExpanded = !isInsurancePendingExplanationExpanded
        }
        insurancePendingExplanation.animateCollapse(0, 0)
        isInsurancePendingExplanationExpanded = false
    }

    private fun setActivationFigures(startDate: LocalDate) {
        val period = LocalDate.now().until(startDate)

        insurancePendingCountdownMonths.text = period.months.toString()
        insurancePendingCountdownDays.text = period.days.toString()

        // insurances is started at mid night
        val midnight = GregorianCalendar().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val millisToMidnight = midnight.timeInMillis - System.currentTimeMillis()
        val hours = ((millisToMidnight / (1000 * 60 * 60)) % 24)
        val minutes = ((millisToMidnight / (1000 * 60)) % 60)

        insurancePendingCountdownHours.text = hours.toString()
        insurancePendingCountdownMinutes.text = minutes.toString()

        // dispose interval if one all ready exists
        setActivationFiguresInterval?.dispose()
        // start interval
        val disposable = Flowable.interval(30, TimeUnit.SECONDS, Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                { setActivationFigures(startDate) }, { Timber.e(it) }
            )
        compositeDisposable += disposable
        setActivationFiguresInterval = disposable
    }
}
