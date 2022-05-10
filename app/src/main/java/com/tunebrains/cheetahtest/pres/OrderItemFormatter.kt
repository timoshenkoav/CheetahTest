package com.tunebrains.cheetahtest.pres

import android.content.res.Resources
import com.tunebrains.cheetahtest.R
import com.tunebrains.cheetahtest.domain.OrderItemEntity
import com.tunebrains.cheetahtest.domain.PackagingType
import java.text.NumberFormat
import java.util.*

class OrderItemFormatter(
    private val resources: Resources
) {
    private val format: NumberFormat = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 2
        currency = Currency.getInstance("usd")
    }

    fun price(item: OrderItemEntity): String {
        return format.format(item.product.price / 100f)
    }

    fun quantity(item: OrderItemEntity): String {
        return item.quantity.toString()
    }

    fun unit(item: OrderItemEntity): String {
        return when (item.type) {
            PackagingType.WEIGHT -> resources.getString(R.string.unit_weight)
            PackagingType.CASE -> resources.getString(R.string.unit_case)
            PackagingType.UNIT -> resources.getString(R.string.unit_unit)
        }
    }

    fun subtotal(data: OrderItemEntity): String {
        return format.format(data.subTotal / 100f)
    }

    fun total(state: MainUiState.Data): String {
        return format.format(state.total / 100f)
    }
}