package com.example.smu

data class ChatList(
    val text: String,
    var time: String,
    val user: Int // 0은 상대방이 보낸 메시지 1은 내가 보낸 메시지 2는 날짜 3은 상대방 이미지 4는 내 이미지
)
