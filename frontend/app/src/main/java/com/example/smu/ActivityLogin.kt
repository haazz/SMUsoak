package com.example.smu

import DatabaseChat
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
    private val databaseHelper: DatabaseChat by lazy{ DatabaseChat.getInstance(applicationContext)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        databaseHelper.deleteChatroom("2")
        val btnSignIn = binding.loginBtnSignin
        val btnFindPw = binding.loginBtnFindpw
        val btnSignUp = binding.loginBtnSingup
        val autoCheck = binding.loginCheck
        var autologin = false

        autoCheck.setOnCheckedChangeListener { check, isChecked ->
            if (isChecked) {
                editor.putBoolean("autologin", true)
                autologin = true
            } else {
                editor.putBoolean("autologin", false)
                autologin = false
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
            val fcm_token = user.getString("fcm token", "")
            val call = RetrofitObject.getRetrofitService.signIn(Retrofit.RequestSignIn(id, pw, fcm_token!!))
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
                                val intent = Intent(this@ActivityLogin, ActivityChat::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@ActivityLogin,"학번 및 비밀번호를 다시 확인해주세요.",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseToken>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnFindPw.setOnClickListener {
            val intent = Intent(this, ActivityTest2::class.java)
            startActivity(intent)
            finish()
        }
    }
}