package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.profile.ProfileRepository
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.util.extensions.default
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.zipWith
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {
    val data: MutableLiveData<ProfileQuery.Data> = MutableLiveData()
    val dirty: MutableLiveData<Boolean> = MutableLiveData<Boolean>().default(false)
    val trustlyUrl: MutableLiveData<String> = MutableLiveData()

    private val disposables = CompositeDisposable()

    init {
        loadProfile()
    }

    fun startTrustlySession() {
        disposables.add(
            profileRepository
                .startTrustlySession()
                .subscribe({ url ->
                    trustlyUrl.postValue(url.startDirectDebitRegistration())
                }, { error ->
                    Timber.e(error)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun saveInputs(emailInput: String, phoneNumberInput: String) {
        val email = data.value?.member()?.email()
        val phoneNumber = data.value?.member()?.phoneNumber()

        val emailObservable: Observable<String?> = if (email != emailInput) {
            profileRepository
                .updateEmail(emailInput)
                .map { it.data()?.updateEmail()?.email() }
        } else Observable.just(null)

        val phoneNumberObservable: Observable<String?> = if (phoneNumber != phoneNumberInput) {
            profileRepository
                .updatePhoneNumber(phoneNumberInput)
                .map { it.data()?.updatePhoneNumber()?.phoneNumber() }
        } else Observable.just(null)

        disposables.add(
            emailObservable
                .zipWith(phoneNumberObservable) { t1, t2 -> Pair(t1, t2) }
                .subscribe({ (email, phoneNumber) ->
                    profileRepository.writeEmailAndPhoneNumberInCache(email, phoneNumber)
                    dirty.value = false
                }, { error ->
                    Timber.e(error, "Failed to update email and/or phone number")
                })
        )
    }

    private fun loadProfile() {
        disposables.add(
            profileRepository.fetchProfile()
                .subscribe({ response ->
                    data.postValue(response)
                }, { error ->
                    Timber.e(error, "Failed to load profile data")
                })
        )
    }

    fun emailChanged(newEmail: String) {
        val currentEmail = data.value?.member()?.email() ?: ""
        if (currentEmail != newEmail && dirty.value != true) {
            dirty.value = true
        }
    }

    fun phoneNumberChanged(newPhoneNumber: String) {
        val currentPhoneNumber = data.value?.member()?.phoneNumber() ?: ""
        if (currentPhoneNumber != newPhoneNumber && dirty.value != true) {
            dirty.value = true
        }
    }

    fun selectCashback(id: String) {
        disposables.add(
            profileRepository.selectCashback(id)
                .subscribe({ response ->
                    response.data()?.selectCashbackOption()?.let { cashback ->
                        profileRepository.writeCashbackToCache(cashback)
                    }
                }, { error ->
                    Timber.e(error, "Failed to select cashback")
                })
        )
    }

    fun refreshBankAccountInfo() {
        disposables.add(
            profileRepository.refreshBankAccountInfo()
                .subscribe({ response ->
                    response.data()?.bankAccount()?.let { bankAccount ->
                        profileRepository.writeBankAccountInfoToCache(bankAccount)
                    }
                }, { error ->
                    Timber.e(error, "Failed to refresh bank account info")
                })
        )
    }
}
