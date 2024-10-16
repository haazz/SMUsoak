package com.example.smu.connection

import com.example.smu.AuthInterceptor
import com.example.smu.BaseUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObject {
    private val getRetrofit by lazy {

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl(BaseUrl.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getRetrofitService : RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}