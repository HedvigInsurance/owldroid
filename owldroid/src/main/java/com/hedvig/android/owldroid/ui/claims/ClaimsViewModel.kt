package com.hedvig.android.owldroid.ui.claims

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.claims.ClaimsQuickAction
import com.hedvig.android.owldroid.data.claims.ClaimsRepository
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class ClaimsViewModel @Inject constructor(
    private val claimsRepository: ClaimsRepository
): ViewModel() {

    val quickActions: MutableLiveData<List<ClaimsQuickAction>> = MutableLiveData()

    private val disposables = CompositeDisposable()

    fun fetchQuickActions(){
        disposables.add(
            claimsRepository.fetchQuickActions()
                .subscribe({ response ->
                    quickActions.postValue(response)
                }, { error ->
                    Timber.e(error, "Failed to fetch claims quick actions")
                })
        )
    }
}
