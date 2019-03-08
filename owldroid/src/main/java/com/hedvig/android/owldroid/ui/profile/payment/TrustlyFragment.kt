package com.hedvig.android.owldroid.ui.profile.payment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_trustly.*
import javax.inject.Inject

class TrustlyFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var profileViewModel: ProfileViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_trustly, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trustlySuccessClose.background.compatSetTint(requireContext().compatColor(R.color.green))
        trustlySuccessClose.setOnClickListener {
            profileViewModel.refreshBankAccountInfo()
            goBack()
        }

        trustlyFailClose.background.compatSetTint(requireContext().compatColor(R.color.pink))
        trustlyFailClose.setOnClickListener {
            goBack()
        }

        trustlyContainer.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }


        profileViewModel.trustlyUrl.observe(this, Observer { url ->
            trustlyContainer.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, requestedUrl: String?, favicon: Bitmap?) {
                    if (requestedUrl == url) {
                        return
                    }
                    view?.stopLoading()
                    requestedUrl?.let { rUrl ->
                        if (rUrl.contains("success")) {
                            showSuccess()
                            return
                        }
                        if (rUrl.contains("fail")) {
                            showFailure()
                            return
                        }
                    }
                }
            }
            trustlyContainer.loadUrl(url)
        })
        profileViewModel.startTrustlySession()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        trustlyContainer.removeAllViews()
        trustlyContainer.destroy()
    }

    private fun goBack() {
        localBroadcastManager.sendBroadcast(Intent("profileNavigation").apply {
            putExtra("action", "back")
        })
    }

    fun showSuccess() {
        trustlyContainer.remove()
        successScreen.show()
    }

    fun showFailure() {
        trustlyContainer.remove()
        failScreen.show()
    }
}