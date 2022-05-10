package com.tunebrains.cheetahtest.domain

import io.reactivex.rxjava3.core.Single

interface CartRepository {
    fun load(): Single<CartEntity>
}