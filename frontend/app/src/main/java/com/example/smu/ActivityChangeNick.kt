package com.example.smu

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivityChangeNickBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityChangeNick : AppCompatActivity() {

    private val binding: ActivityChangeNickBinding by lazy { ActivityChangeNickBinding.inflate(layoutInflater) }
    private lateinit var textNickCheck: TextView
    private lateinit var btnNickCheck: Button
    private lateinit var editNick: EditText
    private val user = MySharedPreference.user
    private val token = user.getString("accessToken", "")
    private val mail = user.getString("mail", "")
    private var nickCheck = false

    private val nicknameWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textNickCheck.visibility= View.INVISIBLE
            val inputText = s.toString()
            if(inputText.length<2 || inputText.length>8) {
                btnNickCheck.isEnabled = false
                btnNickCheck.alpha=0.5f
            }
            else{
                btnNickCheck.isEnabled = true
                btnNickCheck.alpha=1f
            }
            nickCheck=false
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editNick = binding.changeNickEdit
        editNick.addTextChangedListener(nicknameWatcherListener)

        textNickCheck = binding.changeNickTextCheck
        btnNickCheck = binding.changeNickBtnCheck

        binding.changeNickBtnBack.setOnClickListener {
            finish()
        }

        btnNickCheck.setOnClickListener {
            btnNickCheck.isEnabled = false
            val call = RetrofitObject.getRetrofitService.checkNick(editNick.text.toString())
            call.enqueue(object : Callback<Retrofit.ResponseCheckNick> {
                override fun onResponse(call: Call<Retrofit.ResponseCheckNick>, response: Response<Retrofit.ResponseCheckNick>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            textNickCheck.visibility=View.VISIBLE
                            if (responseBody.data.available) {
                                nickCheck=true
                                textNickCheck.text="사용할 수 있는 닉네임입니다."
                                textNickCheck.setTextColor(Color.BLACK)
                            }else{
                                nickCheck=false
                                textNickCheck.text="이미 사용 중인 닉네임입니다."
                                textNickCheck.setTextColor(Color.RED)
                            }
                        }
                    }
                    btnNickCheck.isEnabled = true
                }

                override fun onFailure(call: Call<Retrofit.ResponseCheckNick>, t: Throwable) {
                    btnNickCheck.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        binding.changeNickBtnChange.setOnClickListener {
            if(!nickCheck){
                Toast.makeText(this@ActivityChangeNick,"중복 확인을 해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                val call = RetrofitObject.getRetrofitService.userUpdate("Bearer $token", Retrofit.RequestUpdateInfo(mail!!, editNick.text.toString(), null))
                call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                    override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && responseBody.success) {
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit", errorMessage)
                    }
                })
            }
        }
    }
}