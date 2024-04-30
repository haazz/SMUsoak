package com.example.smu

import android.graphics.BitmapFactory
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
    private val token=user.getString("token","")
    private val mail=user.getString("mail","")
    private val roomList= mutableListOf<Retrofit.Chatroom>()
    private lateinit var recyclerViewChatRoom: RecyclerView
    private lateinit var chatRoomAdapter: AdapterChatRoom

    private val databaseHelper: DatabaseImage by lazy{ DatabaseImage.getInstance(requireContext())}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)

//        val call = RetrofitObject.getRetrofitService.chatList("Bearer $token", mail!!)
//        call.enqueue(object : Callback<Retrofit.ResponseChatroom> {
//            override fun onResponse(call: Call<Retrofit.ResponseChatroom>, response: Response<Retrofit.ResponseChatroom>) {
//                if (response.isSuccessful) {
//                    val response = response.body()
//                    if (response != null) {
//                        if (response.success) {
//                            for (i in response.data){
//                                roomList.add(i)
//                            }
//                        }
//                    }
//                }
//            }
//            override fun onFailure(call: Call<Retrofit.ResponseChatroom>, t: Throwable) {
//                val errorMessage = "Call Failed: ${t.message}"
//                Log.d("Retrofit", errorMessage)
//            }
//        })
//
//        if(roomList.isNotEmpty()){
//            for(i in roomList){
//                for(j in i.mails){
//                    if(databaseHelper.checkExist(j)){
//                        continue
//                    }else{
//                        val call = RetrofitObject.getRetrofitService.profileDown(token!!, j)
//                        call.enqueue(object : Callback<ResponseBody> {
//                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                                if (response.isSuccessful) {
//                                    val byteArray = response.body()!!.bytes()
//                                    databaseHelper.insertImage(j, byteArray)
//                                }
//                            }
//                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                                val errorMessage = "Call Failed: ${t.message}"
//                                Log.d("Retrofit", errorMessage)
//                            }
//                        })
//                    }
//                }
//            }
//        }

        roomList.add(Retrofit.Chatroom(1, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(2, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(3, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(4, listOf("a", "b", "c")))
        roomList.add(Retrofit.Chatroom(5, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(6, listOf("a", "b","a", "b", "c")))
        roomList.add(Retrofit.Chatroom(7, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(8, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(9, listOf("a", "b", "c")))
        roomList.add(Retrofit.Chatroom(10, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(11, listOf("a", "b","a", "b", "c","a", "b", "c")))
        roomList.add(Retrofit.Chatroom(12, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(13, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(14, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(15, listOf("a", "b", "c")))
        roomList.add(Retrofit.Chatroom(16, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(17, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(18, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(19, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(20, listOf("a", "b", "c")))
        roomList.add(Retrofit.Chatroom(21, listOf("a", "b", "c", "a", "b", "c")))
        roomList.add(Retrofit.Chatroom(22, listOf("a", "b")))
        roomList.add(Retrofit.Chatroom(23, listOf("a", "b")))

        recyclerViewChatRoom=binding.fchatRv
        recyclerViewChatRoom.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        chatRoomAdapter=AdapterChatRoom(roomList, requireContext())
        recyclerViewChatRoom.adapter = chatRoomAdapter



        return binding.root
    }
}