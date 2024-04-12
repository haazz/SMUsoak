package com.example.smu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.FragmentChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentChat : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val user=MySharedPreference.user
    private val token=user.getString("token","")
    private val mail=user.getString("mail","")
    private val roomlist= mutableListOf<Retrofit.chatroom>()
    private lateinit var recyclerViewChatRoom: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)

        val call = RetrofitObject.getRetrofitService.chatlist(token!!, mail!!)
        call.enqueue(object : Callback<Retrofit.Responsechatroom> {
            override fun onResponse(call: Call<Retrofit.Responsechatroom>, response: Response<Retrofit.Responsechatroom>) {
                Log.d("chatroom", response.toString())
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null) {
                        if (response.success) {
                            for (i in response.data){
                                roomlist.add(i)
                            }
                            Log.d("chatroom", roomlist.toString())
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Retrofit.Responsechatroom>, t: Throwable) {
                val errorMessage = "Call Failed: ${t.message}"
                Log.d("Retrofit", errorMessage)
            }
        })
        recyclerViewChatRoom=binding.fchatRv



        return binding.root
    }
}