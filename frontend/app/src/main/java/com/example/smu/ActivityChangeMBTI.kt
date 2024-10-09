package com.example.smu

import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivityChangeMbtiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityChangeMBTI : AppCompatActivity() {

    private val binding: ActivityChangeMbtiBinding by lazy { ActivityChangeMbtiBinding.inflate(layoutInflater) }

    private lateinit var numberPicker: NumberPicker
    private val user = Application.user
    private val token = user.getString("accessToken", "")
    private val mail = user.getString("mail", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        numberPicker = binding.changeMbtiPicker

        val array = resources.getStringArray(R.array.mbti)
        val mbtiArray = array.sliceArray(1 until array.size)

        numberPicker.let {
            it.minValue = 0
            it.maxValue = mbtiArray.size - 1
            it.wrapSelectorWheel = true
            it.displayedValues = mbtiArray
        }

        binding.changeMbtiBack.setOnClickListener {
            finish()
        }

        binding.changeMbtiBtn.setOnClickListener {
            val selectedIndex = numberPicker.value
            val selectedValue = mbtiArray[selectedIndex]

            val call = RetrofitObject.getRetrofitService.userUpdate("Bearer $token", Retrofit.RequestUpdateInfo(mail!!, null, selectedValue))
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