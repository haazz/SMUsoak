package com.example.smu

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences

class Application : Application(){
    companion object {
        lateinit var user: SharedPreferences
        lateinit var emotionList: List<Int>
    }

    override fun onCreate() {
        super.onCreate()
        user = getSharedPreferences("user", Context.MODE_PRIVATE)
        emotionList = listOf(
            R.drawable.emotion_1,
            R.drawable.emotion_2,
            R.drawable.emotion_3,
            R.drawable.emotion_4,
            R.drawable.emotion_5,
            R.drawable.emotion_6,
            R.drawable.emotion_7,
            R.drawable.emotion_8,
            R.drawable.emotion_9,
            R.drawable.emotion_10,
            R.drawable.emotion_11,
            R.drawable.emotion_12,
            R.drawable.emotion_13,
            R.drawable.emotion_14,
            R.drawable.emotion_15,
            R.drawable.emotion_16,
            R.drawable.emotion_17,
            R.drawable.emotion_18,
            R.drawable.emotion_19,
            R.drawable.emotion_20,
            R.drawable.emotion_21,
            R.drawable.emotion_22,
            R.drawable.emotion_23,
            R.drawable.emotion_24
        )

        val name = getString(R.string.alarm_name)                    // 채널명
        val channelId = getString(R.string.channel_id)
        val descriptionText = getString(R.string.channel_name)  // 채널 설명
        val importance = NotificationManager.IMPORTANCE_DEFAULT        // 채널 중요도

        val mChannel = NotificationChannel(channelId, name, importance)

        mChannel.description = descriptionText

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        //https://velog.io/@odesay97/%EC%95%B1-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-09-1-%ED%91%B8%EC%8B%9C-%EC%95%8C%EB%A6%BC-%EC%88%98%EC%8B%A0%EA%B8%B0-firebase
    }
}