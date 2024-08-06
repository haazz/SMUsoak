package com.example.smu.connection

import com.google.gson.annotations.SerializedName

class Retrofit {
    //로그인
    data class RequestSignIn(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("password")
        val pw: String,
        @SerializedName("fcmToken")
        val fcmToken: String
    )
    //토큰 재요청
    data class RequestRefreshToken(
        @SerializedName("refreshToken")
        val refreshToken:String
    )
    //회원가입
    data class RequestSignUp(
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
    //토큰 반환
    data class ResponseToken(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: Token
    )
    data class Token(
        @SerializedName("accessToken")
        val accessToken:String,
        @SerializedName("refreshToken")
        val refreshToken:String
    )

    //메일 인증
    data class RequestSendNum(
        @SerializedName("mail")
        val mail: String
    )
    data class ResponseSendNum(
        @SerializedName("success")
        val success: Boolean
    )

    //인증 번호 확인
    data class RequestCheckNum(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("authCode")
        val authCode: String
    )

    //닉에임 중복 확인
    data class ResponseCheckNick(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: CheckNick
    )

    data class CheckNick(
        @SerializedName("available")
        val available: Boolean
    )

    //성공 여부만 반환
    data class ResponseSuccess(
        @SerializedName("success")
        val success: Boolean
    )

    //유저 정보 업데이트
    data class RequestUpdateInfo(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("nickname")
        val nick: String?,
        @SerializedName("mbti")
        val mbti: String?
    )

    //유저 정보 가져오기
    data class RequestUser(
        @SerializedName("mailList")
        val mailList: List<String>
    )
    data class ResponseUser(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: List<UserData>
    )

    data class UserData(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("nickname")
        val nick: String,
        @SerializedName("age")
        val age: Int,
        @SerializedName("gender")
        val gender: String,
        @SerializedName("mbti")
        val mbti: String?,
        @SerializedName("imgUrl")
        val url: String,
        @SerializedName("imgType")
        val type: String
    )

    //채팅룸 리스트 가져오기
    data class ResponseChatroom(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: List<Chatroom>
    )
    data class Chatroom(
        @SerializedName("roomId")
        val roomId: Int,
        @SerializedName("mails")
        val mails: List<String>
    )
    //채팅 이미지 url
    data class ResponseChatImage(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: DownUrl
    )
    data class DownUrl(
        @SerializedName("downloadUrl")
        val downloadUrl: String
    )

    data class RequestTestRoom(
        @SerializedName("mails")
        val mails: MutableList<String>
    )

    data class ResponseTestRoom(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: ChatRoomId
    )

    data class ChatRoomId(
        @SerializedName("chatRoomId")
        val chatRoomId: Boolean
    )
}