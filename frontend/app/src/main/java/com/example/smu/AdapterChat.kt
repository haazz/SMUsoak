package com.example.smu

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.RvChattingBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterChat(private val chatList : MutableList<ChatMessage>,
    private val context : Context) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    private val user = MySharedPreference.user
    private val senderMail = user.getString("mail","")

    private val databaseHelper: DatabaseChatImage by lazy{ DatabaseChatImage.getInstance(context)}

    inner class ViewHolder(binding: RvChattingBinding) : RecyclerView.ViewHolder(binding.root){

        private val myChatConst = binding.rvChattingConstMy
        private val myChat = binding.rvChattingTextMy
        private val myChatTime = binding.rvChattingTimeMy

        private val otherConst1 = binding.rvChattingConst1
        private val otherChat1 = binding.rvChattingChat1
        private val otherTime1 = binding.rvChattingTime1
        private val profile = binding.rvChattingProfile

        private val otherConst2 = binding.rvChattingConst2
        private val otherChat2 = binding.rvChattingChat2
        private val otherTime2 = binding.rvChattingTime2

        private val dateChatConst = binding.rvChattingDay
        private val dateChat = binding.rvChattingDayText

        fun bind(list : ChatMessage) {
            when (list.sender) {
                "$senderMail" -> { // 본인이 보낸 메세지
                    myChatConst.visibility= View.VISIBLE
                    otherConst1.visibility= View.GONE
                    otherConst2.visibility= View.GONE
                    dateChatConst.visibility= View.GONE
                    myChat.text=list.message
                    myChatTime.text=list.time.substring(9)
                }
                "system" -> { // 시스템 메세지
                    dateChatConst.visibility= View.VISIBLE
                    otherConst1.visibility= View.GONE
                    otherConst2.visibility= View.GONE
                    myChatConst.visibility= View.GONE
                    dateChat.text=list.message
                }
                else -> { // 0상대가 보낸 첫 메세지, 2상대가 연속으로 보낸 메세지, 10 상대가 보낸 첫 이미지, 12 상대가 연속으로 보낸 이미지
                    if(list.flag == 0){ //0이면 처음
                        otherConst1.visibility= View.VISIBLE
                        profile.clipToOutline = true
                        otherConst2.visibility= View.GONE
                        myChatConst.visibility= View.GONE
                        dateChatConst.visibility= View.GONE
                        otherChat1.text=list.message
                        otherTime1.text=list.time.substring(9)
                    }else if(list.flag == 2){ // 나머지는 연속
                        otherConst2.visibility= View.VISIBLE
                        otherConst1.visibility= View.GONE
                        myChatConst.visibility= View.GONE
                        dateChatConst.visibility= View.GONE
                        otherChat2.text=list.message
                        otherTime2.text=list.time.substring(9)
                    }else if(list.flag == 10){
                        val url = list.message

//                        val call = RetrofitObject.getRetrofitService.chatImage("Bearer $token", imagePart, room)
//                        call.enqueue(object : Callback<Retrofit.ResponseChatImage> {
//                            override fun onResponse(call: Call<Retrofit.ResponseChatImage>, response: Response<Retrofit.ResponseChatImage>) {
//                                if (response.isSuccessful) {
//                                    val chatImageResponse = response.body()
//                                    if (chatImageResponse != null && chatImageResponse.success) {
//                                        val imageUrl = chatImageResponse.data.downloadUrl
//                                        if(open) {
//                                            Log.d("chatting", chatImageResponse.toString())
//                                            val data = JSONObject()
//                                            data.put("roomId", roomId)
//                                            data.put("message", imageUrl)
//                                            data.put("senderMail", "$sender")
//                                            data.put("time", currentTime)
//                                            data.put("flag", 1)
//                                            stompClient.send("/app/send", data.toString()).subscribe()
//                                            Log.d("chatting", roomId)
//                                        }
//                                    }
//                                }
//                            }
//                            override fun onFailure(call: Call<Retrofit.ResponseChatImage>, t: Throwable) {
//                                val errorMessage = "Call Failed: ${t.message}"
//                                Log.d("Retrofit", errorMessage)
//                            }
//                        })

                    }else if(list.flag == 12){

                    }
                }
            }

            profile.setOnClickListener{

                val builder = AlertDialog.Builder(context,R.style.CustomAlertDialog)
                val view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_profile,
                    null
                )

                // 다이얼로그 텍스트 설정
                builder.setView(view)
                view.findViewById<TextView>(R.id.dProfile_text_age).text = 25.toString()
                view.findViewById<TextView>(R.id.dProfile_text_gender).text = "남"
                view.findViewById<TextView>(R.id.dProfile_text_grade).text = "19학번"
                view.findViewById<TextView>(R.id.dProfile_text_mbti).text = "ISTP"

                val alertDialog = builder.create()

                view.findViewById<ImageButton>(R.id.dProfile_btn_x).setOnClickListener {
                    alertDialog.dismiss()
                }

                view.findViewById<ImageButton>(R.id.dProfile_btn_report).setOnClickListener {
                    Toast.makeText(context,"신고하기",Toast.LENGTH_SHORT).show()
                }

                view.findViewById<ImageButton>(R.id.dProfile_btn_block).setOnClickListener {
                    Toast.makeText(context,"차단하기",Toast.LENGTH_SHORT).show()
                }

                alertDialog.window?.setBackgroundDrawable(ColorDrawable(0)) // 50% 투명도 검정색

                alertDialog.show()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterChat.ViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount() = chatList.size
}