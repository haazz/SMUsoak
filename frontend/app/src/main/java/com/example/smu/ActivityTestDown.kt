package com.example.smu

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivityTestDownBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityTestDown : AppCompatActivity() {

    private val binding: ActivityTestDownBinding by lazy { ActivityTestDownBinding.inflate(layoutInflater) }
    private val user = MySharedPreference.user
    private val mailList = mutableListOf<String>()
    private val urlList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val token = "Bearer "+user.getString("token", "")
        val email = user.getString("mail", "")
        mailList.add("201910911@sangmyung.kr")
        mailList.add("1234")

        Log.d("profile", token)
        binding.btnTestdown.setOnClickListener {
            val call = RetrofitObject.getRetrofitService.userprofile(token, Retrofit.Requestuserprofile(email!!, mailList))
            Log.d("profile", mailList.toString())
            Log.d("profile", token)
            Log.d("profile", email.toString())
            call.enqueue(object : Callback<Retrofit.Responseuserprofile> {
                override fun onResponse(call: Call<Retrofit.Responseuserprofile>, response: Response<Retrofit.Responseuserprofile>) {
                    if (response.isSuccessful) {
                        val response = response.body()
                        if (response != null) {
                            if (response.success) {
                                Log.d("profile", response.data.toString())
                                for (item in response.data) {
                                    val url = item.url as? String
                                    urlList.add(url!!)
                                }
                                Log.d("profile", urlList.toString())
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Retrofit.Responseuserprofile>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }
        binding.btnDown.setOnClickListener {
            val call = RetrofitObject.getRetrofitService.profiledown(token, "1234")
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("profile", response.toString())
                    if (response.isSuccessful) {
                        val byteArray = response.body()!!.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        binding.imageProfile.setImageBitmap(bitmap)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }
    }
}