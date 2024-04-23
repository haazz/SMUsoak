package com.example.smu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.databinding.RvChattingBinding

class AdapterChat(private val chatList : MutableList<ChatMessage>) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    private val user = MySharedPreference.user
    private val senderMail = user.getString("mail","")

    inner class ViewHolder(private val binding: RvChattingBinding) : RecyclerView.ViewHolder(binding.root){

        private val myChatConst = binding.rvChattingMychatConst
        private val myChat = binding.rvChattingMytext
        private val myChatTime = binding.rvChattingMyTime
        private val otherChatConst = binding.rvChattingOtherchatConst
        private val otherChat = binding.rvChattingOthertext
        private val otherChatTime = binding.rvChattingOtherTime
        private val dateChatConst = binding.rvChattingDay
        private val dateChat = binding.rvChattingDayText

        fun bind(list : ChatMessage) {
            when (list.sender) {
                "message $senderMail" -> {
                    myChatConst.visibility= View.VISIBLE
                    otherChatConst.visibility= View.GONE
                    dateChatConst.visibility= View.GONE
                    myChat.text=list.message
                    if(list.time.length>=9){
                        myChatTime.text=list.time.substring(9)
                    }else{
                        myChatTime.text=list.time
                    }
                }
                "system" -> {
                    dateChatConst.visibility= View.VISIBLE
                    otherChatConst.visibility= View.GONE
                    myChatConst.visibility= View.GONE
                    dateChat.text=list.message
                }
                else -> {
                    otherChatConst.visibility= View.VISIBLE
                    myChatConst.visibility= View.GONE
                    dateChatConst.visibility= View.GONE
                    otherChat.text=list.message
                    if(list.time.length>=9){
                        otherChatTime.text=list.time.substring(9)
                    }else{
                        otherChatTime.text=list.time
                    }
                }
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