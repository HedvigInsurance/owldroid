package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.util.react.AsyncStorageNativeReader
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_profile.*
import javax.inject.Inject

class ProfileFragment : Fragment() {
    @Inject
    lateinit var asyncStorageNativeReader: AsyncStorageNativeReader

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var profileViewModel: ProfileViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        } ?: throw Exception("No Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        Toast.makeText(context!!, asyncStorageNativeReader.getKey("@hedvig:token"), Toast.LENGTH_LONG).show()
        observeProfile()
        return view
    }

    private fun observeProfile() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context!!)
        profileViewModel.member.observe(this, Observer {
            profile_loading_spinner.visibility = ProgressBar.GONE
            profile_rows_container.visibility = LinearLayout.VISIBLE
            profile_info_row_name.text = it!!.firstName().or("Test Testerson")
            profile_my_info_row.setOnClickListener {
                val intent = Intent("profileMyInfo")
                localBroadcastManager.sendBroadcast(intent)
            }
        })
    }
}
