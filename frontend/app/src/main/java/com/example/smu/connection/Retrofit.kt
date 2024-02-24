package com.example.smu.connection

import com.google.gson.annotations.SerializedName
import java.util.Objects

class Retrofit {
    //로그인 요청
    data class Requestsignin(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("password")
        val pw: String
    )
    //로그인 응답
    data class Responsesignin(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: token
    )

    data class token(
        @SerializedName("token")
        val token:String
    )
    //회원가입 요청
    data class Requestsignup(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("password")
        val pw: String
    )
    //회원가입 응답
    data class Responsesignup(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: Objects
    )
}