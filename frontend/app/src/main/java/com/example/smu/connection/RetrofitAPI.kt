package com.example.smu.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/authentication/signin")
    fun signin(@Body request: Retrofit.Requestsignin): Call<Retrofit.Responsesignin>

    @POST("/authentication/signup")
    fun signup(@Body request: Retrofit.Requestsignup): Call<Retrofit.Responsesignup>
}