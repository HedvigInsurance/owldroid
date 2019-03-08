package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.profile.ProfileRepository
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.util.extensions.default
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {
    val data: MutableLiveData<ProfileQuery.Data> = MutableLiveData()
    val dirty: MutableLiveData<Boolean> = MutableLiveData<Boolean>().default(false)
    val trustlyUrl: MutableLiveData<String> = MutableLiveData()

    private var dataDisposable: Disposable? = null
    private var trustlyDisposable: Disposable? = null

    init {
        loadProfile()
    }

    fun startTrustlySession() {
        trustlyDisposable = profileRepository
                .startTrustlySession()
                .subscribe({ url ->
                    trustlyUrl.postValue(url.startDirectDebitRegistration())
                }, { error ->
                    Timber.e(error)
                })
    }

    fun saveInputs(emailInput: String, phoneNumberInput: String) {
        val email = data.value?.member()?.email()
        val phoneNumber = data.value?.member()?.phoneNumber()

        if (email != emailInput) {
            profileRepository.updateEmail(emailInput)
        }

        if (phoneNumber != phoneNumberInput) {
            profileRepository.updatePhoneNumber(phoneNumberInput)
        }

        dirty.value = false
    }

    private fun loadProfile() {
        dataDisposable = profileRepository.fetchProfile()
                .subscribe({ response ->
                    data.postValue(response)
                }, { error ->
                    Timber.e(error)
                })
    }

    override fun onCleared() {
        dataDisposable?.dispose()
        trustlyDisposable?.dispose()
        super.onCleared()
    }

    fun emailChanged(newEmail: String) {
        val currentEmail = data.value?.member()?.email()
        if (currentEmail != newEmail) {
            if (dirty.value != true) {
                dirty.value = true
            }
        }
    }

    fun phoneNumberChanged(newPhoneNumber: String) {
        val currentPhoneNumber = data.value?.member()?.phoneNumber()
        if (currentPhoneNumber != newPhoneNumber) {
            if (dirty.value != true) {
                dirty.value = true
            }
        }
    }

    fun selectCashback(id: String) {
        profileRepository.selectCashback(id)
    }

    fun refreshBankAccountInfo() {
        profileRepository.refreshBankAccountInfo()
    }
}
