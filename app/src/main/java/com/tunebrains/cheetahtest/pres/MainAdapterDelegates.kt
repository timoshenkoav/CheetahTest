package com.tunebrains.cheetahtest.pres

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso
import com.tunebrains.cheetahtest.databinding.ViewErrorBinding
import com.tunebrains.cheetahtest.databinding.ViewItemBinding
import com.tunebrains.cheetahtest.databinding.ViewLoadingBinding
import com.tunebrains.cheetahtest.domain.OrderItemEntity

interface MainAdapterItem
object LoadingItem : MainAdapterItem
object RetryItem : MainAdapterItem
data class CartItem(
    val data: OrderItemEntity
) : MainAdapterItem

fun loadingDelegate() =
    adapterDelegateViewBinding<LoadingItem, MainAdapterItem, ViewLoadingBinding>({ layoutInflater, parent ->
        ViewLoadingBinding.inflate(layoutInflater, parent, false)
    }) {}

fun retryDelegate(action: () -> Unit) =
    adapterDelegateViewBinding<RetryItem, MainAdapterItem, ViewErrorBinding>({ layoutInflater, parent ->
        ViewErrorBinding.inflate(layoutInflater, parent, false)
    }) {
        bind {
            binding.retry.setOnClickListener {
                action()
            }
        }
    }

fun cartDelegate(
    picasso: Picasso,
    orderItemFormatter: OrderItemFormatter
) =
    adapterDelegateViewBinding<CartItem, MainAdapterItem, ViewItemBinding>({ layoutInflater, parent ->
        ViewItemBinding.inflate(layoutInflater, parent, false)
    }) {
        bind {
            item.data.product.photo?.let {
                picasso.load(item.data.product.photo).into(binding.image)
            } ?: binding.image.setImageDrawable(null)
            binding.substitutable.isVisible = item.data.substitutable
            binding.name.text = item.data.product.name
            binding.unit.text = orderItemFormatter.unit(item.data)
            binding.price.text = orderItemFormatter.price(item.data)
            binding.quantity.text = orderItemFormatter.quantity(item.data)
            binding.subtotal.text = orderItemFormatter.subtotal(item.data)
        }
    }