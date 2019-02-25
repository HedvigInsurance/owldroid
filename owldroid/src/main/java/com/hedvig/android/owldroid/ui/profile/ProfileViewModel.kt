package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.profile.ProfileRepository
import com.hedvig.android.owldroid.graphql.ProfileQuery
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {
    val data: MutableLiveData<ProfileQuery.Data> = MutableLiveData()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        profileRepository.fetchProfile()
            .subscribe {
                if (it == null) {
                    throw RuntimeException("Something went wrong while loading profile data (was null)")
                }
                data.postValue(it)
            }
    }
}
