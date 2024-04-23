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

        private val myChat = binding.rvChattingMychatConst
        private val otherChat = binding.rvChattingOtherchatConst
        private val dateChat = binding.rvChattingDay

        fun bind(list : ChatMessage) {
            when (list.sender) {
                "message $senderMail" -> {
                    myChat.visibility= View.VISIBLE
                }
                "system" -> {
                    dateChat.visibility= View.VISIBLE
                }
                else -> {
                    otherChat.visibility= View.VISIBLE
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