package com.example.smu

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class MySharedPreference : Application(){
    companion object {
        lateinit var user: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        user = getSharedPreferences("user", Context.MODE_PRIVATE)
    }
}