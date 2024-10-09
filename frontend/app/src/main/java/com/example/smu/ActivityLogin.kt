package com.example.smu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var id: String
    private lateinit var pw: String

    //자동 로그인 설정
    private val user = Application.user
    private val editor = user.edit()

    private val databaseHelper: DatabaseChat by lazy{ DatabaseChat.getInstance(applicationContext)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        databaseHelper.deleteChatroom("1")

        val btnSignIn = binding.loginBtnSignin
        val btnFindPw = binding.loginBtnFindpw
        val btnSignUp = binding.loginBtnSingup

        btnSignIn.setOnClickListener {
            btnSignIn.isEnabled = false
            id = binding.loginEditId.text.toString()+"@sangmyung.kr"
            pw = binding.loginEditPw.text.toString()
            val fcmToken = user.getString("fcm token", "")
            Log.d("fcmToken", fcmToken.toString())
            val call = RetrofitObject.getRetrofitService.signIn(Retrofit.RequestSignIn(id, pw, fcmToken!!))
            call.enqueue(object : Callback<Retrofit.ResponseToken> {
                override fun onResponse(call: Call<Retrofit.ResponseToken>, response: Response<Retrofit.ResponseToken>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            if(responseBody.success) {
                                val accessToken = responseBody.data.accessToken
                                Log.d("accessToken", accessToken)
                                val refreshToken = responseBody.data.refreshToken
                                editor.putString("accessToken", accessToken)
                                editor.putString("refreshToken", refreshToken)
                                editor.putString("mail", id)
                                editor.apply()
                                val intent = Intent(this@ActivityLogin, ActivityMain::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@ActivityLogin,"학번 및 비밀번호를 다시 확인해 주세요.",Toast.LENGTH_SHORT).show()
                        btnSignIn.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseToken>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                    btnSignIn.isEnabled = true
                }
            })
        }

        btnFindPw.setOnClickListener {
            val intent = Intent(this, ActivityFindpw::class.java)
            startActivity(intent)
            finish()
        }

        btnSignUp.setOnClickListener{
            val intent = Intent(this, ActivitySingup::class.java)
            startActivity(intent)
            finish()
        }
    }
}