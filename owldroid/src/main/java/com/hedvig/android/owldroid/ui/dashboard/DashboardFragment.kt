package com.hedvig.android.owldroid.ui.dashboard

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.observe
import com.hedvig.android.owldroid.util.extensions.remove
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_coinsured.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
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


        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.DASHBOARD_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))

        loadData()
    }

    private fun loadData() {
        dashboardViewModel.data.observe(this) {
            loadingSpinner.remove()

            personalCoverageCard.setOnClickListener {
                if (personalCoverageCardOpen) {
                    personalCoveragePerilContainer.removeAllViews()
                    personalCoverageCardOpen = false
                    return@setOnClickListener
                }
                val image = ImageView(requireContext())
                image.setImageResource(R.drawable.ic_legal)

                personalCoveragePerilContainer.addView(image)
                personalCoverageCardOpen = true
            }
        }
    }
}
