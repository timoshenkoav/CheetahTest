package com.tunebrains.cheetahtest.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

class ApiPackagingTypeAdapter {
    @ToJson
    fun toJson(type: ApiPackagingType): String = type.code

    @FromJson
    fun fromJson(value: String): ApiPackagingType = ApiPackagingType.fromString(value)
}
enum class ApiPackagingType(val code: String) {
    UNIT("unit"),
    CASE("case"),
    WEIGHT("weight");
    companion object {
        fun fromString(type: String) = values().associateBy(ApiPackagingType::code)[type] ?: error("Unknown type")
    }
}

@JsonClass(generateAdapter = true)
data class ProductApiEntity(
    @Json(name = "name")
    val name: String,
    @Json(name = "unit_photo_filename")
    val unitPhoto: String?,
    @Json(name = "pack_photo_file")
    val packPhoto: String?,
    @Json(name = "weight_photo_filename")
    val weightPhoto: String?,
    @Json(name = "unit_price")
    val unitPrice: Long,
    @Json(name = "case_price")
    val casePrice: Long,
    @Json(name = "weight_price")
    val weightPrice: Long
)

@JsonClass(generateAdapter = true)
data class OrderItemApiEntity(
    @Json(name = "quantity")
    val quantity: Double,
    @Json(name = "sub_total")
    val subTotal: Long,
    @Json(name = "packaging_type")
    val packagingType: ApiPackagingType,
    @Json(name = "substitutable")
    val substitutable: Boolean,
    @Json(name = "product")
    val product: ProductApiEntity
)

@JsonClass(generateAdapter = true)
data class CartApiEntity(
    @Json(name = "cart_total")
    val total: Long,
    @Json(name = "order_items_information")
    val items: List<OrderItemApiEntity>
)