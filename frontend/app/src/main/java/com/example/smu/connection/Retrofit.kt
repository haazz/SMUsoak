package com.example.smu.connection

import com.google.gson.annotations.SerializedName

class Retrofit {
    //순서 1. 로그인 2. 회원가입 3. 메일 인증 4. 인증 번호 확인 5. 성공여부만 반환 6. 유저 프로필 가져오기
    //1. 로그인
    data class Requestsignin(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("password")
        val pw: String
    )

    //2.회원가입
    data class Requestsignup(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("password")
        val pw: String,
        @SerializedName("age")
        val age: Int,
        @SerializedName("gender")
        val gender: String, //M W로 작성
        @SerializedName("mbti")
        val mbti: String?,
        @SerializedName("nickname")
        val nickname: String
    )
    data class Responsetoken(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: token
    )
    data class token(
        @SerializedName("token")
        val token:String
    )

    //3. 메일 인증
    data class Requestsendnum(
        @SerializedName("mail")
        val mail: String
    )
    data class Responsesendnum(
        @SerializedName("success")
        val success: Boolean
    )

    //4. 인증 번호 확인
    data class Requestchecknum(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("authCode")
        val authCode: String
    )

    //5. 성공 여부만 반환
    data class Responsesuccess(
        @SerializedName("success")
        val success: Boolean
    )

    //6. 유저 프로필 가져오기
    data class Requestuserprofile(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("mailList")
        val maillist: List<String>
    )
    data class Responseuserprofile(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: List<mailList>
    )

    data class mailList(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("type")
        val type: String
    )
}