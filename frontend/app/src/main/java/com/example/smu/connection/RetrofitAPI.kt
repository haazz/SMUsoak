package com.example.smu.connection

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.ArrayList

interface RetrofitAPI {
    //로그인
    @POST("/api/v1/auth/signin")
    fun signin(@Body request: Retrofit.Requestsignin): Call<Retrofit.Responsetoken>

    //인증 메일 전송
    @POST("/api/v1/auth/mail/send-code")
    fun sendnum(@Body request: Retrofit.Requestsendnum): Call<Retrofit.Responsesendnum>

    //인증 번호 확인
    @POST("/api/v1/auth/mail/verification")
    fun checknum(@Body request: Retrofit.Requestchecknum): Call<Retrofit.Responsesuccess>

    //회원가입
    @POST("/api/v1/auth/signup")
    fun signup(@Body request: Retrofit.Requestsignup): Call<Retrofit.Responsetoken>

    //프로필 업데이트
    @Multipart
    @POST("/api/v1/user/update/img")
    fun profile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("info") info: RequestBody): Call<Retrofit.Responsesuccess>

    //프로필 가져오기
    @POST("/api/v1/user/imgs")
    fun userprofile(
        @Header("Authorization") token: String,
        @Body request: Retrofit.Requestuserprofile): Call<Retrofit.Responseuserprofile>

    //프로필 다운로드
    @GET("/api/v1/download/img/{fileName}")
    fun profiledown(
        @Header("Authorization") token: String,
        @Path("fileName") fileName: String): Call<ResponseBody>
}