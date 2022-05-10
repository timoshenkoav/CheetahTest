package com.tunebrains.cheetahtest.data

import com.tunebrains.cheetahtest.domain.CartEntity
import com.tunebrains.cheetahtest.domain.OrderItemEntity
import com.tunebrains.cheetahtest.domain.PackagingType
import com.tunebrains.cheetahtest.domain.ProductItemEntity
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class CartRepositoryImplTest {
    @Test
    fun shouldReturnDataForUnit() {
        val cartApiEntity = CartApiEntity(
            10, listOf(
                OrderItemApiEntity(
                    1.0, 10, ApiPackagingType.UNIT, false,
                    ProductApiEntity("Unit", "unit", "pack", "weight", 10, 20, 30)
                )
            )
        )
        val api = mockk<CartApi> {
            every { list() } returns Single.just(cartApiEntity)
        }
        val obj = CartRepositoryImpl(api)
        obj.load().test().assertValue(
            CartEntity(
                10, listOf(
                    OrderItemEntity(
                        1.0,
                        10,
                        PackagingType.UNIT,
                        false,
                        ProductItemEntity("Unit", "unit", 10)
                    )
                )
            ))
    }
    @Test
    fun shouldReturnDataForCase() {
        val cartApiEntity = CartApiEntity(
            10, listOf(
                OrderItemApiEntity(
                    1.0, 10, ApiPackagingType.CASE, false,
                    ProductApiEntity("Case", "unit", "pack", "weight", 10, 20, 30)
                )
            )
        )
        val api = mockk<CartApi> {
            every { list() } returns Single.just(cartApiEntity)
        }
        val obj = CartRepositoryImpl(api)
        obj.load().test().assertValue(
            CartEntity(
                10, listOf(
                    OrderItemEntity(
                        1.0,
                        10,
                        PackagingType.CASE,
                        false,
                        ProductItemEntity("Case", "pack", 20)
                    )
                )
            ))
    }
    @Test
    fun shouldReturnDataForWeight() {
        val cartApiEntity = CartApiEntity(
            10, listOf(
                OrderItemApiEntity(
                    1.0, 10, ApiPackagingType.WEIGHT, false,
                    ProductApiEntity("Weight", "unit", "pack", "weight", 10, 20, 30)
                )
            )
        )
        val api = mockk<CartApi> {
            every { list() } returns Single.just(cartApiEntity)
        }
        val obj = CartRepositoryImpl(api)
        obj.load().test().assertValue(
            CartEntity(
                10, listOf(
                    OrderItemEntity(
                        1.0,
                        10,
                        PackagingType.WEIGHT,
                        false,
                        ProductItemEntity("Weight", "weight", 30)
                    )
                )
            ))
    }
}