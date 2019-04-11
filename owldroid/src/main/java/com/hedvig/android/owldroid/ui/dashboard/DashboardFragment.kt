package com.hedvig.android.owldroid.ui.dashboard

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
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
import com.hedvig.android.owldroid.type.DirectDebitStatus
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import com.hedvig.android.owldroid.util.extensions.observe
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.view.animateCollapse
import com.hedvig.android.owldroid.util.extensions.view.animateExpand
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.extensions.view.show
import com.hedvig.android.owldroid.util.interpolateTextKey
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.loading_spinner.*
import org.threeten.bp.LocalDate
import java.util.*
import javax.inject.Inject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.concurrent.TimeUnit


class DashboardFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var dashboardViewModel: DashboardViewModel

    private var personalCoverageCardOpen: Boolean = false

    private var isInsurancePendingExplanationExpanded = false

    private var setActivationFiguresInterval: Disposable? = null
    private val compositeDisposable = CompositeDisposable()

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.loggedInNavigationHost)
    }

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
                setupDirectDebitStatus(data.directDebitStatus())
                setupInsuranceStatusStatus(data.insurance().status(), data.insurance().activeFrom())
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

    private fun setupInsuranceStatusStatus(insuranceStatus: InsuranceStatus, activeFrom: LocalDate?) {
        insurancePending.remove()
        insuranceActive.remove()
        when (insuranceStatus) {
            InsuranceStatus.ACTIVE -> {
                insuranceActive.show()
            }
            InsuranceStatus.INACTIVE,
            InsuranceStatus.INACTIVE_WITH_START_DATE -> {
                insurancePending.show()
                activeFrom?.let { localDate ->
                    insurancePendingCountDownContainer.show()
                    insurancePendingLoadingAnimation.remove()

                    setActivationFigures(localDate)
                    val formattedString = localDate.format(DateTimeFormatter.ofPattern("d LLLL yyyy"))
                    insurancePendingExplanation.text = interpolateTextKey(getString(R.string.DASHBOARD_DIRECT_DEBIT_STATUS_PENDING_HAS_START_DATE_EXPLANATION), "START_DATE" to formattedString)
                } ?: run {
                    insurancePendingCountDownContainer.remove()
                    insurancePendingLoadingAnimation.show()

                    insurancePendingLoadingAnimation.playAnimation()
                    insurancePendingExplanation.text = getString(R.string.DASHBOARD_DIRECT_DEBIT_STATUS_PENDING_NO_START_DATE_EXPLANATION)
                }
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
            InsuranceStatus.`$UNKNOWN`,
            InsuranceStatus.PENDING,
            InsuranceStatus.TERMINATED -> {
            }
        }
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
        compositeDisposable.add(disposable)
        setActivationFiguresInterval = disposable
    }
}
