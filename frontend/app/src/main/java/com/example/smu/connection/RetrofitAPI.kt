package com.example.smu.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/user/test")
    fun signup(@Body request: Retrofit.signup): Call<Retrofit.Responsesignup>
}