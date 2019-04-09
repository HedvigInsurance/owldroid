package com.hedvig.android.owldroid.ui.claims

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.data.claims.ClaimsQuickAction
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.claims.quickaction.QuickActionsAdapter
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.hide
import com.hedvig.android.owldroid.util.extensions.setupLargeTitle
import com.hedvig.android.owldroid.util.extensions.show
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_claims.*
import kotlinx.android.synthetic.main.loading_spinner.*
import timber.log.Timber
import javax.inject.Inject

class ClaimsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var claimsViewModel: ClaimsViewModel

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
            fetchQuickActions()
            quickActions.observe(this@ClaimsFragment, Observer { quickActions ->
                quickActions?.let { setupQuickActions(it) } ?: handleNoQuickActions()
            })
        }
    }

    private fun setupQuickActions(quickActions: List<ClaimsQuickAction>) {
        loadingSpinner.hide()
        quickChoicesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        quickChoicesRecyclerView.adapter = QuickActionsAdapter(quickActions) {
            navController.navigate(R.id.action_claimsFragment_to_quickActionClaimsFragment)
        }
    }

    private fun handleNoQuickActions() {
        //TODO: UI
        Timber.i("No claims quick actions found")
    }
}
