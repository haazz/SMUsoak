package com.example.smu

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.FragmentChatBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentChat : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val user=MySharedPreference.user
    private val token="Bearer " + user.getString("token","")
    private val mail=user.getString("mail","")
    private lateinit var recyclerViewChatRoom: RecyclerView
    private lateinit var chatRoomAdapter: AdapterChatRoom

    private val databaseHelper: DatabaseImage by lazy{ DatabaseImage.getInstance(requireContext())}

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)

        recyclerViewChatRoom=binding.fchatRv

        getChatRooms()

        return binding.root
    }

    private fun getChatRooms() {
        val callChatList = RetrofitObject.getRetrofitService.chatList(token, mail!!)
        callChatList.enqueue(object : Callback<Retrofit.ResponseChatroom> {
            override fun onResponse(call: Call<Retrofit.ResponseChatroom>, response: Response<Retrofit.ResponseChatroom>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val rooms = responseBody.data
                        loadImagesForChatRooms(rooms.toMutableList())
                    }
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseChatroom>, t: Throwable) {
                val errorMessage = "Call Failed: ${t.message}"
                Log.d("Retrofit", errorMessage)
            }
        })
    }

    private fun loadImagesForChatRooms(rooms: MutableList<Retrofit.Chatroom>) {
        for (room in rooms) {
            val pairList = mutableListOf<Pair<String, String>>()
            val call = RetrofitObject.getRetrofitService.userprofile(token, Retrofit.RequestUserProfile(mail!!, room.mails))
            call.enqueue(object : Callback<Retrofit.ResponseUserProfile> {
                override fun onResponse(call: Call<Retrofit.ResponseUserProfile>, response: Response<Retrofit.ResponseUserProfile>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            if (responseBody.success) {
                                for (item in responseBody.data) {
                                    val email = item.mail
                                    val url = item.url
                                    pairList.add(Pair(email, url))
                                }
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseUserProfile>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
            for ((email, url) in pairList) {
                if (!databaseHelper.checkExist(email)) {
                    val callDown = RetrofitObject.getRetrofitService.profileDown(token, url)
                    callDown.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val byteArray = response.body()?.bytes()
                                if (byteArray != null) {
                                    databaseHelper.insertImage(mail, byteArray)
                                }
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

        // 이미지 가져오는 요청이 완료되면 채팅방 목록을 업데이트
        updateChatRoomList(rooms)
    }

    private fun updateChatRoomList(rooms: MutableList<Retrofit.Chatroom>) {
        recyclerViewChatRoom.layoutManager = LinearLayoutManager(requireContext())
        chatRoomAdapter = AdapterChatRoom(rooms, requireContext())
        recyclerViewChatRoom.adapter = chatRoomAdapter
    }

}