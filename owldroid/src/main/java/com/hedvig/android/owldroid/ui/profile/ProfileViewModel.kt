package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.os.Looper
import com.hedvig.android.owldroid.data.profile.ProfileRepository
import com.hedvig.android.owldroid.graphql.ProfileQuery
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository): ViewModel() {

    val member = MutableLiveData<ProfileQuery.Member>()
    val insurance = MutableLiveData<ProfileQuery.Insurance>()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        profileRepository.fetchProfile {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                member.value = it!!.member()
                insurance.value = it.insurance()
            }
        }
    }
}