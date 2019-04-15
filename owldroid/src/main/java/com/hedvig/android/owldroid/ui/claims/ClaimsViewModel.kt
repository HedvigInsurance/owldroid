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
    val titleAndBulletPoint: MutableLiveData<CommonClaimQuery.AsTitleAndBulletPoints> = MutableLiveData()

    private val disposables = CompositeDisposable()

    init {
        fetchCommonClaims()
    }

    fun fetchCommonClaims() {
        Timber.i("fetchCommonClaims OkHttp")
        val disposable = claimsRepository.fetchCommonClaims().subscribe(
            { data.postValue(it) },
            { error ->
                Timber.e(error, "Failed to fetch claims data")
            })
        disposables.add(disposable)
    }

    fun setCommonClaimByTitle(title: String) {
        val l = data.value?.commonClaims()?.first { it.layout() is CommonClaimQuery.AsTitleAndBulletPoints && it.title() == title }?.layout() as CommonClaimQuery.AsTitleAndBulletPoints

        titleAndBulletPoint.postValue(l        )
    }
}
