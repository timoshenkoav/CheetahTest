package com.tunebrains.cheetahtest.domain

enum class PackagingType {
    UNIT,
    CASE,
    WEIGHT
}

data class ProductItemEntity(
    val name: String,
    val photo:String?,
    val price:Long
)

data class OrderItemEntity(
    val quantity:Double,
    val subTotal:Long,
    val type: PackagingType,
    val substitutable: Boolean,
    val product:ProductItemEntity
)
data class CartEntity(
    val total:Long,
    val items:List<OrderItemEntity>
)