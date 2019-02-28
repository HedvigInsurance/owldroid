package com.hedvig.android.owldroid.ui.profile.myinfo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_my_info.*
import timber.log.Timber
import javax.inject.Inject

class MyInfoFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var profileViewModel: ProfileViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        profileViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        } ?: throw Exception("No Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(
            R.layout.fragment_my_info,
            container,
            false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        collapsingToolbar.title = resources.getString(R.string.my_info_title)
        collapsingToolbar.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", "back")
            localBroadcastManager.sendBroadcast(intent)

        }
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val dirty = profileViewModel.dirty.value
        if (dirty == null || !dirty) {
            menu.removeItem(R.id.save)
        }
        super.onPrepareOptionsMenu(menu)
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            sphereContainer.show()

            sphere.drawable.compatSetTint(ContextCompat.getColor(context!!, R.color.dark_purple))
            contactDetailsContainer.show()

            name.text = "${profileData!!.member().firstName()}\n${profileData.member().lastName()}"
            emailInput.setText(profileData.member().email() ?: "")
            phoneNumberInput.setText(profileData.member().phoneNumber() ?: "")

            emailInput.onChange { value ->
                profileViewModel.emailChanged(value)
            }

            phoneNumberInput.onChange { value ->
                profileViewModel.phoneNumberChanged(value)
            }

            profileViewModel.dirty.observe(this, Observer { dirty ->
                Timber.i("Dirty changed value")
                if (dirty != null && dirty) {
                    activity?.invalidateOptionsMenu()
                }
            })
        })
    }
}
