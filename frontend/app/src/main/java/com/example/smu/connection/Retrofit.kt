package com.example.smu.connection

import com.google.gson.annotations.SerializedName

class Retrofit {

    data class Requestsignin(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("password")
        val pw: String
    )

    data class Responsesignin(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: token
    )

    data class token(
        @SerializedName("token")
        val token:String
    )
}