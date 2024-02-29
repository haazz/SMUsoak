package com.example.smu.connection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    private const val BASE_URL = "http://ec2-43-200-30-120.ap-northeast-2.compute.amazonaws.com:8080"

    private val getRetrofit by lazy {

        val clientBuilder = OkHttpClient.Builder()

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getRetrofitService : RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}