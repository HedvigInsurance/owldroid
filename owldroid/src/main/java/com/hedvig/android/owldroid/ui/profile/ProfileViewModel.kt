package com.hedvig.android.owldroid.ui.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.hedvig.android.owldroid.data.profile.ProfileRepository
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.service.Referrals
import com.hedvig.android.owldroid.service.RemoteConfig
import com.hedvig.android.owldroid.service.RemoteConfigData
import com.hedvig.android.owldroid.util.LiveEvent
import com.hedvig.android.owldroid.util.Optional
import com.hedvig.android.owldroid.util.extensions.default
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.zipWith
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val referrals: Referrals,
    private val remoteConfig: RemoteConfig
) :
    ViewModel() {
    val data: MutableLiveData<ProfileQuery.Data> = MutableLiveData()
    val dirty: MutableLiveData<Boolean> = MutableLiveData<Boolean>().default(false)
    val trustlyUrl: LiveEvent<String> = LiveEvent()
    val firebaseLink: MutableLiveData<Uri> = MutableLiveData()
    val remoteConfigData: MutableLiveData<RemoteConfigData> = MutableLiveData()

    private val disposables = CompositeDisposable()

    init {
        loadProfile()
        loadRemoteConfig()
    }

    private fun loadRemoteConfig() {
        disposables.add(
            remoteConfig
                .fetch()
                .subscribe(
                    { remoteConfigData.postValue(it) },
                    { error ->
                        Timber.e(error, "Failed to fetch RemoteConfig data")
                    })
        )
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

        val emailObservable = if (email != emailInput) {
            profileRepository
                .updateEmail(emailInput)
                .map { Optional.Some(it.data()?.updateEmail()?.email()) }
        } else Observable.just(Optional.None)

        val phoneNumberObservable = if (phoneNumber != phoneNumberInput) {
            profileRepository
                .updatePhoneNumber(phoneNumberInput)
                .map { Optional.Some(it.data()?.updatePhoneNumber()?.phoneNumber()) }
        } else Observable.just(Optional.None)

        disposables.add(
            emailObservable
                .zipWith(phoneNumberObservable) { t1, t2 -> Pair(t1, t2) }
                .subscribe({ (email, phoneNumber) ->
                    profileRepository.writeEmailAndPhoneNumberInCache(email.getOrNull(), phoneNumber.getOrNull())
                    dirty.postValue(false)
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
                    response.data()?.let { data ->
                        data.bankAccount()?.let { bankAccount ->
                            profileRepository.writeBankAccountInfoToCache(bankAccount)
                        } ?: Timber.e("Failed to refresh bank account info")
                    } ?: Timber.e("Failed to refresh bank account info")
                }, { error ->
                    Timber.e(error, "Failed to refresh bank account info")
                })
        )
    }

    fun generateReferralLink(memberId: String) {
        remoteConfigData.value?.let { data ->
            disposables.add(
                referrals.generateFirebaseLink(memberId, data)
                    .subscribe({ uri ->
                        firebaseLink.postValue(uri)
                    }, { error ->
                        Timber.e(error)
                    })
            )
        }
    }

    fun logout(callback: () -> Unit) {
        disposables.add(
            profileRepository
                .logout()
                .subscribe({
                    callback()
                }, { error ->
                    Timber.e(error, "Failed to log out")
                })
        )
    }
}
