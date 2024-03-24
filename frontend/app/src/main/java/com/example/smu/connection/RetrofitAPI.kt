package com.example.smu.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {
    //로그인
    @POST("/authentication/signin")
    fun signin(@Body request: Retrofit.Requestsignin): Call<Retrofit.Responsetoken>

    //인증 메일 전송
    @POST("/authentication/sendAuthCode")
    fun sendnum(@Body request: Retrofit.Requestsendnum): Call<Retrofit.Responsesendnum>

    //인증 번호 확인
    @POST("/authentication/mailVerification")
    fun checknum(@Body request: Retrofit.Requestchecknum): Call<Retrofit.Responsechecknum>

    //회원가입
    @POST("/authentication/createUser")
    fun signup(@Body request: Retrofit.Requestsignup): Call<Retrofit.Responsetoken>
}