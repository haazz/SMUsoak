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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentChat : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val user=MySharedPreference.user
    private val token="Bearer " + user.getString("accessToken","")
    private val userMail=user.getString("mail","")
    private val userNickList = hashMapOf<Int, MutableList<String>>()
    private val userMailList = hashMapOf<Int, MutableList<String>>()
    private lateinit var recyclerViewChatRoom: RecyclerView
    private lateinit var chatRoomAdapter: AdapterChatRoom

    private val databaseProfile: DatabaseProfileImage by lazy{ DatabaseProfileImage.getInstance(requireContext())}

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

    //룸 아이디와 채팅방 안에 있는 사람의 메일 리스트를 가져옴
    private fun getChatRooms() {
        val callChatList = RetrofitObject.getRetrofitService.chatList(token, userMail!!)
        callChatList.enqueue(object : Callback<Retrofit.ResponseChatroom> {
            override fun onResponse(call: Call<Retrofit.ResponseChatroom>, response: Response<Retrofit.ResponseChatroom>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val rooms = responseBody.data
                        Log.d("chat room", rooms.toString())
                        loadUserInfoForChatRooms(rooms.toMutableList())
                    }
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseChatroom>, t: Throwable) {
                val errorMessage = "Call Failed: ${t.message}"
                Log.d("Retrofit get room", errorMessage)
            }
        })
    }

    private fun loadUserInfoForChatRooms(rooms: MutableList<Retrofit.Chatroom>){
        var count = rooms.size

        for (room in rooms) {
            val call = RetrofitObject.getRetrofitService.userInfo(token, Retrofit.RequestUser(room.mails))
            call.enqueue(object : Callback<Retrofit.ResponseUser> {
                override fun onResponse(call: Call<Retrofit.ResponseUser>, response: Response<Retrofit.ResponseUser>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody!!.success) {
                            for (item in responseBody.data) {
                                val mail = item.mail
                                if(databaseProfile.checkExist(mail)) {
                                    if(item.date != getCurrentISOTime()){

                                    }
                                }
                            }
                        }
                    }
                    count--
                    if (count == 0) {
                        updateChatRoomList(rooms)
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseUser>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit get info", errorMessage)
                }
            })
        }
    }

    private fun updateChatRoomList(rooms: MutableList<Retrofit.Chatroom>) {
        context?.let { ctx ->
            recyclerViewChatRoom.layoutManager = LinearLayoutManager(ctx)
            chatRoomAdapter = AdapterChatRoom(rooms, ctx, userNickList, userMailList)
            recyclerViewChatRoom.adapter = chatRoomAdapter
        } ?: run {
            Log.d("FragmentChat", "Context is null")
        }
    }

    fun getCurrentISOTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        return currentDateTime.format(formatter)
    }
}