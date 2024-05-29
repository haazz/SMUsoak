package com.example.smu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.databinding.RvChattingBinding

class AdapterChat(private val chatList : MutableList<ChatMessage>) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    private val user = MySharedPreference.user
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

        private val dateChatConst = binding.rvChattingDay
        private val dateChat = binding.rvChattingDayText

        fun bind(list : ChatMessage) {
            when (list.sender) {
                "message $senderMail" -> {
                    myChatConst.visibility= View.VISIBLE
                    otherConst1.visibility= View.GONE
                    otherConst2.visibility= View.GONE
                    dateChatConst.visibility= View.GONE
                    myChat.text=list.message
                    myChatTime.text=list.time.substring(9)
                }
                "system" -> {
                    dateChatConst.visibility= View.VISIBLE
                    otherConst1.visibility= View.GONE
                    otherConst2.visibility= View.GONE
                    myChatConst.visibility= View.GONE
                    dateChat.text=list.message
                }
                else -> {
                    otherConst1.visibility= View.VISIBLE
                    profile.clipToOutline = true
                    otherConst2.visibility= View.GONE
                    myChatConst.visibility= View.GONE
                    dateChatConst.visibility= View.GONE
                    myChatTime.text=list.time.substring(9)
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