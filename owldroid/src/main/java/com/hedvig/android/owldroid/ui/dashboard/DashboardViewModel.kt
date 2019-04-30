package com.hedvig.android.owldroid.ui.dashboard

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.chat.ChatRepository
import com.hedvig.android.owldroid.data.dashboard.DashboardRepository
import com.hedvig.android.owldroid.graphql.DashboardQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    val dashboardRepository: DashboardRepository,
    val chatRepository: ChatRepository
) : ViewModel() {

    val data = MutableLiveData<DashboardQuery.Data>()

    val disposables = CompositeDisposable()

    init {
        loadData()
    }

    private fun loadData() {
        disposables += dashboardRepository
            .fetchDashboard()
            .subscribe({ response ->
                response.data()?.let { data.postValue(it) }
            }, { Timber.e(it) })
    }

    fun triggerFreeTextChat(done: () -> Unit) {
        disposables += chatRepository
            .triggerFreeTextChat()
            .subscribe({ done() }, { Timber.e(it) })
    }
}
