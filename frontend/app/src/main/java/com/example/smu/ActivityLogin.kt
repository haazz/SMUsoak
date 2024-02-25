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

        val btn_singin = binding.loginBtnSignin
        val btn_findpw = binding.loginBtnFindpw
        val btn_signup = binding.loginBtnSingup
        val checkauto = binding.loginCheck

        checkauto.setOnCheckedChangeListener { check, isChecked ->
            if (isChecked) {
                editor.putBoolean("autologin", true)
            } else {
                editor.putBoolean("autologin", false)
            }
            editor.apply()
        }

        btn_signup.setOnClickListener{
            val intent = Intent(this, ActivitySingup::class.java)
            startActivity(intent)
            finish()
        }

        btn_singin.setOnClickListener {
            id = binding.loginEditId.text.toString()+"@sangmyung.kr"
            pw = binding.loginEditPw.text.toString()
            val call = RetrofitObject.getRetrofitService.signin(Retrofit.Requestsignin(id, pw))
            call.enqueue(object : Callback<Retrofit.Responsesignin> {
                override fun onResponse(call: Call<Retrofit.Responsesignin>, response: Response<Retrofit.Responsesignin>) {
                    if (response.isSuccessful) {
                        val response = response.body()
                        if(response != null){
                            if(response.success) {
                                val token = response.data.token
                                editor.putString("id", id)
                                editor.putString("jwt", token)
                                editor.putString("pw", pw)
                                editor.apply()
                                val intent =
                                    Intent(this@ActivityLogin, ActivityMain::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@ActivityLogin,"로그인 정보가 없습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@ActivityLogin,"api연결 실패",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Retrofit.Responsesignin>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btn_findpw.setOnClickListener {
            val intent = Intent(this, ActivityProfile::class.java)
            startActivity(intent)
            finish()
        }
    }
}