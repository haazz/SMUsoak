package com.example.smu

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Calendar

class FirebaseMessag : FirebaseMessagingService() {

    private val databaseHelper: DatabaseChat by lazy{ DatabaseChat.getInstance(applicationContext)}
    private lateinit var chatList: MutableList<ChatMessage>

    override fun onNewToken(token: String) {
        val user = MySharedPreference.user
        val editor = user.edit()
        editor.putString("fcm token", token)
        editor.apply()
        Log.d("FCM Log", "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val roomId = message.data["roomId"]!!
        val flag = message.data["flag"]!!.toInt()
        val mail = message.data["mail"]!!
        val mes = message.notification!!.body.toString()

        chatList = databaseHelper.getAllMessages(roomId)

        val currentTime = getCurrentTime()
        val currentDate = getCurrentDate()

        if(chatList.size == 0) {
            chatList.add(ChatMessage("system", currentDate, currentTime, flag))
            databaseHelper.insertMessage(roomId,"system",currentDate,currentTime, flag)
        }else{
            val lTime = chatList[chatList.size-1].time.split(" ")
            val cTime = currentTime.split(" ")
            if(lTime[0]!=cTime[0]) {
                chatList.add(ChatMessage("system", currentDate, currentTime, flag))
                databaseHelper.insertMessage(roomId, "system", currentDate, currentTime, flag)
            }
        }

        if(chatList[chatList.size-1].sender == mail){
            if(flag != 1){
                databaseHelper.insertMessage(roomId, mail, mes, currentTime, 2)
            }else{
                databaseHelper.insertMessage(roomId, mail, mes, currentTime, 12)
            }
        }else{
            if(flag != 1){
                databaseHelper.insertMessage(roomId, mail, mes, currentTime, 0)
            }else{
                databaseHelper.insertMessage(roomId, mail, mes, currentTime, 10)
            }
        }

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, "New message received: $mes", Toast.LENGTH_SHORT).show()
        }
    }

    //날짜를 yyyy 년 mm 월 dd 일로 가져옴
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        return dateFormat.format(calendar.time)
    }

    //오전 or 오후 몇 시인지 변환
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd a hh:mm")
        return dateFormat.format(calendar.time)
    }
}