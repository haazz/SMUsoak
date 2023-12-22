package com.example.smu.connection

import com.google.gson.annotations.SerializedName

class Retrofit {

    data class signup(
        @SerializedName("studentid")
        val id: String,
        @SerializedName("password")
        val pw: String
    )

    data class Responsesignup(
        @SerializedName("studentid")
        val id: String,
        @SerializedName("password")
        val pw: String
    )
}