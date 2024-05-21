package com.example.smu.connection

import com.google.gson.annotations.SerializedName

class Retrofit {
    //순서 1. 로그인 2. 회원가입 3. 메일 인증 4. 인증 번호 확인 5. 닉네임 중복 확인 6. 성공여부만 반환 7. 유저 정보 가져오기 8. 채팅룸 리스트 가져오기
    //1. 로그인
    data class RequestSignIn(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("password")
        val pw: String,
        @SerializedName("fcmToken")
        val fcmToken: String
    )

    //2.회원가입
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
    data class ResponseToken(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: Token
    )
    data class Token(
        @SerializedName("token")
        val token:String
    )

    //3. 메일 인증
    data class RequestSendNum(
        @SerializedName("mail")
        val mail: String
    )
    data class ResponseSendNum(
        @SerializedName("success")
        val success: Boolean
    )

    //4. 인증 번호 확인
    data class RequestCheckNum(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("authCode")
        val authCode: String
    )

    //5. 닉에임 중복 확인
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

    //6. 성공 여부만 반환
    data class ResponseSuccess(
        @SerializedName("success")
        val success: Boolean
    )

    //7. 유저 정보 가져오기
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

    //8. 채팅룸 리스트 가져오기
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