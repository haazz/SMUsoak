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
    private val token="Bearer " + user.getString("accessToken","")
    private val userMail=user.getString("mail","")
    private val userNickList = hashMapOf<Int, MutableList<String>>()
    private val userMailList = hashMapOf<Int, MutableList<String>>()
    private lateinit var recyclerViewChatRoom: RecyclerView
    private lateinit var chatRoomAdapter: AdapterChatRoom

    private val databaseHelper: DatabaseProfileImage by lazy{ DatabaseProfileImage.getInstance(requireContext())}

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

    //유저 정보 다운
    private fun loadUserInfoForChatRooms(rooms: MutableList<Retrofit.Chatroom>) {
        val userMap = hashMapOf<String, String>()
        var count = rooms.size

        for (room in rooms) {
            val call = RetrofitObject.getRetrofitService.userInfo(token, Retrofit.RequestUser(room.mails))
            call.enqueue(object : Callback<Retrofit.ResponseUser> {
                override fun onResponse(call: Call<Retrofit.ResponseUser>, response: Response<Retrofit.ResponseUser>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            if (responseBody.success) {
                                for (item in responseBody.data) {
                                    if (userNickList.containsKey(room.roomId)) {
                                        userNickList[room.roomId]!!.add(item.nick)
                                        userMailList[room.roomId]!!.add(item.mail)
                                    } else {
                                        userNickList[room.roomId] = mutableListOf(item.nick)
                                        userMailList[room.roomId] = mutableListOf(item.mail)
                                    }
                                    userMap[item.mail]=item.url
                                    Log.d("userMap", item.toString())
                                }
                            }
                        }
                    }
                    count--
                    if (count == 0) {
                        loadUserProfileImage(userMap, rooms)
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseUser>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit get info", errorMessage)
                }
            })
        }
    }

    //유저 이미지 다운
    private fun loadUserProfileImage(userMap: HashMap<String, String>, rooms: MutableList<Retrofit.Chatroom>) {
        Log.d("userMap", userMap.toString())
        for (mail in userMap.keys) {
            if (userMap[mail] != null) {
                val url = userMap[mail]!!
                val callDown = RetrofitObject.getRetrofitService.profileDown(token, url)
                callDown.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val byteArray = response.body()?.bytes()
                            if (byteArray != null) {
                                Log.d("image down", "success")
                                databaseHelper.insertImage(mail, byteArray)
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit get image", errorMessage)
                    }
                })
            }
        }

        // 이미지 가져오는 요청이 완료되면 채팅방 목록을 업데이트
        updateChatRoomList(rooms)
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
}