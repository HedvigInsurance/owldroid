package com.hedvig.android.owldroid.ui.claims

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.claims.ClaimsRepository
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class ClaimsViewModel @Inject constructor(
    private val claimsRepository: ClaimsRepository
) : ViewModel() {

    val data: MutableLiveData<CommonClaimQuery.Data> = MutableLiveData()
    val selectedSubViewData: MutableLiveData<CommonClaimQuery.CommonClaim> = MutableLiveData()

    private val disposables = CompositeDisposable()

    init {
        fetchCommonClaims()
    }

    fun fetchCommonClaims() {
        val disposable = claimsRepository.fetchCommonClaims().subscribe(
            { data.postValue(it) },
            { error ->
                Timber.e(error, "Failed to fetch claims data")
            })
        disposables.add(disposable)
    }

    fun setSelectedSubViewData(selectedSubView: CommonClaimQuery.CommonClaim) =
        selectedSubViewData.postValue(selectedSubView)
}
