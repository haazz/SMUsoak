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
    private val user = MySharedPreference.user
    private val editor = user.edit()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val btnSignIn = binding.loginBtnSignin
        val btnFindPw = binding.loginBtnFindpw
        val btnSignUp = binding.loginBtnSingup
        val autoCheck = binding.loginCheck
        var autologin = false

        autoCheck.setOnCheckedChangeListener { check, isChecked ->
            autologin = if (isChecked) {
                editor.putBoolean("autologin", true)
                true
            } else {
                editor.putBoolean("autologin", false)
                false
            }
            editor.apply()
        }

        btnSignUp.setOnClickListener{
            val intent = Intent(this, ActivitySingup::class.java)
            startActivity(intent)
            finish()
        }

        btnSignIn.setOnClickListener {
            id = binding.loginEditId.text.toString()+"@sangmyung.kr"
            pw = binding.loginEditPw.text.toString()
            val fcmToken = user.getString("fcm token", "")
            val call = RetrofitObject.getRetrofitService.signIn(Retrofit.RequestSignIn(id, pw, fcmToken!!))
            call.enqueue(object : Callback<Retrofit.ResponseToken> {
                override fun onResponse(call: Call<Retrofit.ResponseToken>, response: Response<Retrofit.ResponseToken>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            if(responseBody.success) {
                                val token = responseBody.data.token
                                editor.putString("token", token)
                                editor.putString("mail", id)
                                if(autologin){
                                    editor.putString("id", id)
                                    editor.putString("pw", pw)
                                }
                                editor.apply()
                                val intent = Intent(this@ActivityLogin, ActivityMain::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@ActivityLogin,"학번 및 비밀번호를 다시 확인해 주세요.",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseToken>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnFindPw.setOnClickListener {

        }
    }
}