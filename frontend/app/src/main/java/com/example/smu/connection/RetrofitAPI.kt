package com.example.smu.connection

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Part

interface RetrofitAPI {
    //로그인
    @POST("/authentication/signin")
    fun signin(@Body request: Retrofit.Requestsignin): Call<Retrofit.Responsetoken>

    //인증 메일 전송
    @POST("/authentication/sendAuthCode")
    fun sendnum(@Body request: Retrofit.Requestsendnum): Call<Retrofit.Responsesendnum>

    //인증 번호 확인
    @POST("/authentication/mailVerification")
    fun checknum(@Body request: Retrofit.Requestchecknum): Call<Retrofit.Responsesuccess>

    //회원가입
    @POST("/authentication/createUser")
    fun signup(@Body request: Retrofit.Requestsignup): Call<Retrofit.Responsetoken>

    //프로필 업데이트
    @POST("/user/update/img")
    fun profile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Body request: Retrofit.Requestprofile): Call<Retrofit.Responsesuccess>

    //프로필 가져오기
    @POST("/user/imgs")
    fun userprofile(
        @Header("Authorization") token: String,
        @Body request: Retrofit.Requestuserprofile): Call<Retrofit.Responseuserprofile>
}