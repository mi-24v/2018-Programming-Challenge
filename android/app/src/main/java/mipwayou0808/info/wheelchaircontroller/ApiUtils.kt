package mipwayou0808.info.wheelchaircontroller

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * API helper
 */

fun RetrofitBuilder(): Retrofit{
    return Retrofit.Builder()
            .baseUrl("http://localhost:256/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}
