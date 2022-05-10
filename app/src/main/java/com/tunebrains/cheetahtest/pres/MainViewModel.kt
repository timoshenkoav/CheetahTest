package com.tunebrains.cheetahtest.pres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tunebrains.cheetahtest.domain.CartRepository
import com.tunebrains.cheetahtest.domain.OrderItemEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

sealed class MainUiState {
    object LOADING : MainUiState()
    data class Error(val ex: Throwable) : MainUiState()
    data class Data(val total: Long, val items: List<OrderItemEntity>) : MainUiState()
}

class MainViewModel(
    cartRepository: CartRepository,
    uiScheduler: Scheduler
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val uiData = MutableLiveData<MainUiState>()
    val data: LiveData<MainUiState> = uiData

    private val querySubject = BehaviorSubject.create<String>()
    private val retrySubject = PublishSubject.create<Long>()

    init {
        querySubject.onNext("")
        compositeDisposable.add(
            retrySubject.startWithItem(0).flatMap {
                loadData(cartRepository)
            }.observeOn(uiScheduler)
                .subscribeBy(onNext = {
                    uiData.value = it
                })
        )
    }

    private fun loadData(cartRepository: CartRepository) =
        cartRepository.load().flatMapObservable { cart ->
            querySubject.map<MainUiState> { query ->
                MainUiState.Data(cart.total, cart.items.filter {
                    query.isEmpty() || it.product.name.contains(query, true)
                }.sortedBy { it.product.name })
            }
        }.onErrorReturn {
            MainUiState.Error(it)
        }.startWithItem(MainUiState.LOADING)

    fun queryChanged(newQuery: String) {
        querySubject.onNext(newQuery)
    }

    fun retry() {
        retrySubject.onNext(System.currentTimeMillis())
    }
}