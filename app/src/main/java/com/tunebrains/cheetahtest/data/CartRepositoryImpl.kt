package com.tunebrains.cheetahtest.data

import com.tunebrains.cheetahtest.domain.CartEntity
import com.tunebrains.cheetahtest.domain.CartRepository
import com.tunebrains.cheetahtest.domain.OrderItemEntity
import com.tunebrains.cheetahtest.domain.PackagingType
import com.tunebrains.cheetahtest.domain.ProductItemEntity
import io.reactivex.rxjava3.core.Single

internal class CartRepositoryImpl(
    private val cartApi: CartApi
) : CartRepository {
    override fun load(): Single<CartEntity> {
        return cartApi.list().map { cartList ->
            cartList.toDomain()
        }
    }
}

private fun CartApiEntity.toDomain(): CartEntity {
    return CartEntity(
        total = this.total,
        items = this.items.map {
            it.toDomain()
        }
    )
}

private fun OrderItemApiEntity.toDomain(): OrderItemEntity {
    return OrderItemEntity(
        quantity = this.quantity,
        subTotal = this.subTotal,
        type = this.packagingType.toDomain(),
        substitutable = this.substitutable,
        product = this.product.toDomain(this.packagingType)
    )
}

private fun ProductApiEntity.toDomain(packagingType: ApiPackagingType): ProductItemEntity {
    return ProductItemEntity(
        name = this.name,
        photo = when (packagingType) {
            ApiPackagingType.UNIT -> this.unitPhoto
            ApiPackagingType.WEIGHT -> this.weightPhoto
            ApiPackagingType.CASE -> this.packPhoto
        }.takeUnless { it.isNullOrEmpty() },
        price = when (packagingType) {
            ApiPackagingType.UNIT -> this.unitPrice
            ApiPackagingType.WEIGHT -> this.weightPrice
            ApiPackagingType.CASE -> this.casePrice
        }
    )
}

private fun ApiPackagingType.toDomain(): PackagingType {
    return when (this) {
        ApiPackagingType.CASE -> PackagingType.CASE
        ApiPackagingType.UNIT -> PackagingType.UNIT
        ApiPackagingType.WEIGHT -> PackagingType.WEIGHT
    }
}