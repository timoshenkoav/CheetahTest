package com.tunebrains.cheetahtest.pres

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.picasso.Picasso
import com.tunebrains.cheetahtest.data.ApiPackagingTypeAdapter
import com.tunebrains.cheetahtest.data.CartApi
import com.tunebrains.cheetahtest.data.CartRepositoryImpl
import com.tunebrains.cheetahtest.domain.CartRepository
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CheetahApp:Application() {
    override fun onCreate() {
        super.onCreate()
        val apiModule = module {
            single {
                Moshi.Builder()
                    .add(ApiPackagingTypeAdapter())
                    .build()
            }
            single {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
                val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .cache(Cache(cacheDir, 10 * 1024 * 1024))
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.mocky.io/")
                    .addConverterFactory(MoshiConverterFactory.create(get()))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
                    .client(client)
                    .build()
                retrofit.create(CartApi::class.java)
            }
        }
        val dataModule = module {
            single<CartRepository> {
                CartRepositoryImpl(get())
            }
        }
        val domainModule = module {

        }
        val presModule = module {
            viewModel {
                MainViewModel(get(),get())
            }
            single {
                Picasso.get()
            }
            single {
                OrderItemFormatter(resources)
            }
            single {
                AndroidSchedulers.mainThread()
            }
        }
        startKoin {
            androidContext(this@CheetahApp)
            modules(apiModule,dataModule, domainModule, presModule)
        }
    }
}