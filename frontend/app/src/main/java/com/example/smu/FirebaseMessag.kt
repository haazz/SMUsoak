package com.example.smu

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseMessag : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM Log", "Refreshed token: $token")
    }
}