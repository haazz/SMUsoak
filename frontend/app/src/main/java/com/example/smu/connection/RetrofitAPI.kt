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
import retrofit2.http.Url

interface RetrofitAPI {
    //로그인
    @POST("/api/v1/auth/signin")
    fun signIn(@Body request: Retrofit.RequestSignIn): Call<Retrofit.ResponseToken>

    //토큰 재반환
    @POST("/api/v1/auth/refresh-token")
    fun token(@Body request: Retrofit.RequestRefreshToken): Call<Retrofit.ResponseToken>

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

    //유저 정보 업데이트
    @POST("/api/v1/user/update/info")
    fun userUpdate(
        @Header("Authorization") token: String,
        @Body request: Retrofit.RequestUpdateInfo): Call<Retrofit.ResponseSuccess>

    //채팅 이미지 전송
    @Multipart
    @POST("/api/v1/chat/update/img")
    fun chatImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("info") info: RequestBody): Call<Retrofit.ResponseChatImage>

    //유저정보 가져오기
    @POST("/api/v1/user/info")
    fun userInfo(
        @Header("Authorization") token: String,
        @Body request: Retrofit.RequestUser): Call<Retrofit.ResponseUser>

    //이미지 다운로드
    @GET
    fun profileDown(
        @Header("Authorization") token: String,
        @Url url: String): Call<ResponseBody>

    //채팅룸 리스트 가져오기
    @GET("/api/v1/chat/room/list/{mail}")
    fun chatList(
        @Header("Authorization") token: String,
        @Path("mail") mail: String): Call<Retrofit.ResponseChatroom>

    //채팅룸 만들기 테스트
    @POST("/api/v1/test/chat-room")
    fun makeRoom(
        @Header("Authorization") token: String,
        @Body request: Retrofit.RequestTestRoom): Call<Retrofit.ResponseTestRoom>
}