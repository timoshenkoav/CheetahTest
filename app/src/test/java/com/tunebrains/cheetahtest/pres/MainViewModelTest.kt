package com.tunebrains.cheetahtest.pres

import com.jraska.livedata.TestObserver
import com.tunebrains.cheetahtest.domain.CartEntity
import com.tunebrains.cheetahtest.domain.CartRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeoutException

@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

    @Test
    fun shouldRespondWithData(){
        val cartEntity = CartEntity(10, emptyList())
        val repo = mockk<CartRepository>{
            every { load() } returns Single.just(cartEntity)
        }
        val uiScheduler = TestScheduler()
        val underTest = MainViewModel(repo, uiScheduler)
        uiScheduler.triggerActions()
        TestObserver.test(underTest.data)
            .assertValue(MainUiState.Data(10, emptyList()))

    }

    @Test
    fun shouldRespondWithError(){

        val repo = mockk<CartRepository>{
            every { load() } returns Single.error(TimeoutException())
        }
        val uiScheduler = TestScheduler()
        val underTest = MainViewModel(repo, uiScheduler)
        uiScheduler.triggerActions()
        TestObserver.test(underTest.data)
            .assertValue {
                it is MainUiState.Error
            }

    }
}