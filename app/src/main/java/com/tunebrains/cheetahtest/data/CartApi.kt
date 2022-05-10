package com.tunebrains.cheetahtest.data

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CartApi {
    @GET("/v2/59c791ed1100005300c39b93")
    fun list(): Single<CartApiEntity>
}