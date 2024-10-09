package com.example.smu

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.example.smu.databinding.RvChattingBinding
import java.time.LocalDateTime

class AdapterChat(private val chatList : MutableList<ChatMessage>,
    private val context : Context) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    private val user = Application.user
    private val senderMail = user.getString("mail","")

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

        private val myImageConst = binding.rvChattingConstMyImage
        private val myImageTime = binding.rvChattingMyImageTime
        private val myImage = binding.rvChattingMyimage

        private val dateChatConst = binding.rvChattingDay
        private val dateChat = binding.rvChattingDayText

        fun bind(list : ChatMessage) {

            fun myChatting(){
                myChatConst.visibility= View.VISIBLE
                otherConst1.visibility= View.GONE
                otherConst2.visibility= View.GONE
                dateChatConst.visibility= View.GONE
                myImageConst.visibility= View.GONE
                myChat.text=list.message
                myChatTime.text=list.time.substring(9)
            }

            fun myImageChatting(){
                myChatConst.visibility= View.GONE
                otherConst1.visibility= View.GONE
                otherConst2.visibility= View.GONE
                dateChatConst.visibility= View.GONE
                myImageConst.visibility= View.VISIBLE
                myImageTime.text=list.time.substring(9)
            }

            when (list.flag) {
                2 -> { //연속 문자
                    if(list.sender == senderMail){
                        myChatting()
                    }else{
                        myChatConst.visibility= View.GONE
                        otherConst1.visibility= View.GONE
                        otherConst2.visibility= View.VISIBLE
                        dateChatConst.visibility= View.GONE
                        otherChat2.text=list.message
                        otherTime2.text=list.time.substring(9)
                    }
                }
                12 -> { // 연속 이미지
                    if(list.sender == senderMail){
                        myImageChatting()
                        val widthPx = dpToPx(context, 300)
                        Glide.with(context)
                            .load(list.message)
                            .override(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)  // 가로를 300dp로 제한
                            .transform(FitCenter())  // 세로 비율 유지
                            .into(myImage)
                        myImage.clipToOutline = true
                    }
                }
                0 -> { // 다른 문자
                    if(list.sender == senderMail){
                        myChatting()
                    }else{
                        myChatConst.visibility= View.GONE
                        otherConst1.visibility= View.VISIBLE
                        otherConst2.visibility= View.GONE
                        dateChatConst.visibility= View.GONE
                        otherChat1.text=list.message
                        otherTime1.text=list.time.substring(9)
                    }
                }
                10 -> { // 다른 이미지
                    if(list.sender == senderMail){
                        myImage.clipToOutline = true
                        val widthPx = dpToPx(context, 300)
                        Glide.with(context)
                            .load(list.message)
                            .override(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)  // 가로를 300dp로 제한
                            .transform(FitCenter())  // 세로 비율 유지
                            .into(myImage)
                    }
                    myImageChatting()
                }
                3 -> { // 시스템
                    dateChatConst.visibility= View.VISIBLE
                    otherConst1.visibility= View.GONE
                    otherConst2.visibility= View.GONE
                    myChatConst.visibility= View.GONE
                    dateChat.text=list.message
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
        Log.d("이미지 추적 : 화면 업데이트", LocalDateTime.now().toString())
    }

    override fun getItemCount() = chatList.size

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}