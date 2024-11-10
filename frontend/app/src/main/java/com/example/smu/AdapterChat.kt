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
    private val userMail = user.getString("mail","")
    private val databaseHelper: DatabaseProfileImage by lazy{ DatabaseProfileImage.getInstance(context)}

    inner class ViewHolder(binding: RvChattingBinding) : RecyclerView.ViewHolder(binding.root){

        private val myChatConst = binding.rvChattingConstMy
        private val myChat = binding.rvChattingTextMy
        private val myChatTime = binding.rvChattingTimeMy

        private val otherImageConst1 = binding.rvChattingConstOtherImage1
        private val otherImageProfile = binding.rvChattingOtherImageProfile
        private val otherImage1 = binding.rvChattingOtherImage1
        private val otherImageTime1 = binding.rvChattingOtherImageTime1

        private val otherImageConst2 = binding.rvChattingConstOtherImage2
        private val otherImage2 = binding.rvChattingOtherImage2
        private val otherImageTime2 = binding.rvChattingOtherImageTime2

        private val otherConst1 = binding.rvChattingConst1
        private val otherChat1 = binding.rvChattingChat1
        private val otherTime1 = binding.rvChattingTime1
        private val otherNick = binding.rvChattingNick
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
                myChat.text=list.message
                myChatTime.text=list.time.substring(9)
            }

            fun myImageChatting(){
                myImageConst.visibility= View.VISIBLE
                myImageTime.text=list.time.substring(9)
            }

            when (list.flag) {
                2 -> { //연속 문자
                    if(list.sender == userMail){
                        myChatting()
                    }else{
                        otherConst2.visibility= View.VISIBLE
                        otherChat2.text=list.message
                        otherTime2.text=list.time.substring(9)
                    }
                }
                12 -> { // 연속 이미지
                    if(list.sender == userMail){
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
                    if(list.sender == userMail){
                        myChatting()
                    }else{
                        otherConst1.visibility= View.VISIBLE
                        otherNick.text = list.senderNick
                        profile.clipToOutline = true
                        Glide.with(context)
                            .load(databaseHelper.getImage(list.sender))
                            .into(profile)
                        otherChat1.text=list.message
                        otherTime1.text=list.time.substring(9)
                    }
                }
                10 -> { // 다른 이미지
                    if(list.sender == userMail){
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