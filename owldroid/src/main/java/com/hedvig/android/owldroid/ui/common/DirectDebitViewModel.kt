package com.hedvig.android.owldroid.ui.common

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.debit.DirectDebitRepository
import com.hedvig.android.owldroid.graphql.DirectDebitQuery
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class DirectDebitViewModel @Inject constructor(
    private val directDebitRepository: DirectDebitRepository
) : ViewModel() {
    val data: MutableLiveData<DirectDebitQuery.Data> = MutableLiveData()

    private val disposables = CompositeDisposable()

    init {
        fetchDirectDebit()
    }

    private fun fetchDirectDebit() {
        val disposable = directDebitRepository.fetchDirectDebit()
            .subscribe({ response ->
                data.postValue(response)
            }, { error ->
                Timber.e(error, "Failed to load direct debit data")
            })
        disposables.add(disposable)
    }

    fun refreshDirectDebitStatus() {
        val disposable = directDebitRepository.refreshDirectdebitStatus()
            .subscribe({ response ->
                response.data()?.let { data ->
                    directDebitRepository.writeDirectDebitStatusToCache(data.directDebitStatus())
                } ?: Timber.e("Failed to refresh direct debit status")
            }, { error ->
                Timber.e(error, "Failed to refresh direct debit status")
            })

        disposables.add(disposable)
    }
}
