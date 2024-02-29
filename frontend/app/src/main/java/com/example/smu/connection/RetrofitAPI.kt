package com.example.smu.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/authentication/signin")
    fun signin(@Body request: Retrofit.Requestsignin): Call<Retrofit.Responsesignin>

    //인증 메일 전송
    @POST("/authentication/sendAuthCode ")
    fun sendnum(@Body request: Retrofit.Requestsendnum): Call<Retrofit.Responsesendnum>
    //인증 번호 확인
    @POST("/authentication/mailVerification")
    fun checknum(@Body request: Retrofit.Requestchecknum): Call<Retrofit.Responsechecknum>
}