package com.example.smu

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseMessag : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        val user = MySharedPreference.user
        val editor = user.edit()
        editor.putString("fcm token", token)
        editor.apply()
        Log.d("FCM Log", "Refreshed token: $token")
    }
}