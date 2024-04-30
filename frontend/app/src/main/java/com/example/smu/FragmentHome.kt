package com.example.smu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentHome : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val user = MySharedPreference.user
    private val token = user.getString("token", "None")
    private val mailList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        mailList.add("201910911@sangmyung.kr")
        mailList.add("201910912@sangmyung.kr")

        binding.testbtn.setOnClickListener {
            val call = RetrofitObject.getRetrofitService.makeRoom("Bearer $token", Retrofit.RequestTestRoom(mailList))
            call.enqueue(object : Callback<Retrofit.ResponseTestRoom> {
                override fun onResponse(call: Call<Retrofit.ResponseTestRoom>, response: Response<Retrofit.ResponseTestRoom>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            if(responseBody.success) {
                                Toast.makeText(requireContext(),"방이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseTestRoom>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        return binding.root
    }
}