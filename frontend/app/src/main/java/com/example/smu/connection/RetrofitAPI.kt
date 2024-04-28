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

interface RetrofitAPI {
    //로그인
    @POST("/api/v1/auth/signin")
    fun signIn(@Body request: Retrofit.RequestSignIn): Call<Retrofit.ResponseToken>

    //인증 메일 전송
    @POST("/api/v1/auth/mail/send-code")
    fun sendNum(@Body request: Retrofit.RequestSendNum): Call<Retrofit.ResponseSendNum>

    //인증 번호 확인
    @POST("/api/v1/auth/mail/verification")
    fun checkNum(@Body request: Retrofit.RequestCheckNum): Call<Retrofit.ResponseSuccess>

    //닉네임 중복 확인
    @GET("/api/v1/user/check/nickname/{nick}")
    fun checkNick(@Path("nick") nick: String): Call<Retrofit.ResponseCheckNick>

    //회원가입
    @POST("/api/v1/auth/signup")
    fun signUp(@Body request: Retrofit.RequestSignUp): Call<Retrofit.ResponseToken>

    //프로필 업데이트
    @Multipart
    @POST("/api/v1/user/update/img")
    fun profile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("info") info: RequestBody): Call<Retrofit.ResponseSuccess>

    //프로필 가져오기
    @POST("/api/v1/user/imgs")
    fun userprofile(
        @Header("Authorization") token: String,
        @Body request: Retrofit.RequestUserProfile): Call<Retrofit.ResponseUserProfile>

    //프로필 다운로드
    @GET("/api/v1/download/img/{fileName}")
    fun profileDown(
        @Header("Authorization") token: String,
        @Path("fileName") fileName: String): Call<ResponseBody>

    //채팅룸 리스트 가져오기
    @GET("/api/v1/chat/room/list/{mail}")
    fun chatList(
        @Header("Authorization") token: String,
        @Path("mail") mail: String): Call<Retrofit.ResponseChatroom>
}