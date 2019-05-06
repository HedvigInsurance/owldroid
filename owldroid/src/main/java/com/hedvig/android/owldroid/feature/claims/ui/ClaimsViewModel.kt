package com.hedvig.android.owldroid.feature.claims.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.chat.ChatRepository
import com.hedvig.android.owldroid.feature.claims.data.ClaimsRepository
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class ClaimsViewModel @Inject constructor(
    private val claimsRepository: ClaimsRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    val data: MutableLiveData<CommonClaimQuery.Data> = MutableLiveData()
    val selectedSubViewData: MutableLiveData<CommonClaimQuery.CommonClaim> = MutableLiveData()

    private val disposables = CompositeDisposable()

    init {
        fetchCommonClaims()
    }

    fun fetchCommonClaims() {
        disposables += claimsRepository.fetchCommonClaims().subscribe(
            { data.postValue(it) },
            { error ->
                Timber.e(error, "Failed to fetch claims data")
            })
    }

    fun setSelectedSubViewData(selectedSubView: CommonClaimQuery.CommonClaim) =
        selectedSubViewData.postValue(selectedSubView)

    fun triggerClaimsChat(claimTypeId: String? = null, done: () -> Unit) {
        disposables += claimsRepository
            .triggerClaimsChat(claimTypeId)
            .subscribe({ done() }, { Timber.e(it) })
    }

    fun triggerFreeTextChat(done: () -> Unit) {
        disposables += chatRepository
            .triggerFreeTextChat()
            .subscribe({ done() }, { Timber.e(it) })
    }
}
