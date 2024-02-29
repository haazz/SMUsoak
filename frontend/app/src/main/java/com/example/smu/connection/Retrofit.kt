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
        val data: signup
    )
    data class signup(
        @SerializedName("status")
        val status:Int,
        @SerializedName("code")
        val code:String,
        @SerializedName("message")
        val message:String
    )

    //메일에 인증 번호 전송 요청
    data class Requestsendnum(
        @SerializedName("mail")
        val mail: String
    )
    //메일에 인증 번호 전송 응답
    data class Responsesendnum(
        @SerializedName("success")
        val success: Boolean
    )

    //인증 번호 확인 요청
    data class Requestchecknum(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("authCode")
        val authCode: String
    )
    //인증 번호 확인 응답
    data class Responsechecknum(
        @SerializedName("success")
        val success: Boolean
    )
}