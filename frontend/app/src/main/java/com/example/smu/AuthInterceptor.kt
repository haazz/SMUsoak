package com.example.smu

import android.util.Log
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {

    @Volatile
    private var isRefreshing = false // 토큰 재발급 중인지 여부를 나타내는 플래그

    private val sharedPreferences = Application.user

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var currentResponse = chain.proceed(request)

        if (currentResponse.code == 401) {
            Log.d("Token finish", "토큰이 만료되어서 새로운 토큰을 받아옵니다.")
            if (!isRefreshing) { // 토큰 재발급 중이 아닌 경우에만 실행
                synchronized(this) {
                    isRefreshing = true
                    val tokens = getNewToken()
                    isRefreshing = false

                    if (tokens != null) {
                        request = request.newBuilder()
                            .header("Authorization", "Bearer ${tokens.accessToken}")
                            .build()
                        currentResponse.close() // 기존 응답을 닫음
                        currentResponse = chain.proceed(request) // 새로운 요청을 실행
                    }
                }
            }
        }

        return currentResponse
    }

    private fun getNewToken(): Tokens? {
        val currentToken = sharedPreferences.getString("RefreshToken", "")
        val call = RetrofitObject.getRetrofitService.token(Retrofit.RequestRefreshToken(currentToken!!))

        return try {
            val retrofitResponse = call.execute()
            if (retrofitResponse.isSuccessful) {
                val responseBody = retrofitResponse.body()
                if (responseBody != null && responseBody.success) {
                    val accessToken = responseBody.data.accessToken
                    val refreshToken = responseBody.data.refreshToken
                    saveTokens(accessToken, refreshToken)
                    Tokens(accessToken, refreshToken)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: IOException) {
            Log.e("AuthInterceptor", "Network error: ${e.message}")
            null
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }
}

data class Tokens(val accessToken: String, val refreshToken: String)