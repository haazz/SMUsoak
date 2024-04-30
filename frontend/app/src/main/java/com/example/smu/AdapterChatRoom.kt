package com.example.smu

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.connection.Retrofit
import com.example.smu.databinding.RvChatRoomBinding
import com.example.smu.databinding.RvChattingBinding

class AdapterChatRoom(private val roomList : MutableList<Retrofit.Chatroom>, private val context: Context) : RecyclerView.Adapter<AdapterChatRoom.ViewHolder>() {

    private val databaseHelper: DatabaseImage by lazy{ DatabaseImage.getInstance(context)}

    inner class ViewHolder(private val binding: RvChatRoomBinding) : RecyclerView.ViewHolder(binding.root){

        private val constTwo = binding.rvChatRoomConstTwo
        private val constThree = binding.rvChatRoomConstThree
        private val constFour = binding.rvChatRoomConstFour

        @SuppressLint("SetTextI18n")
        fun bind(list : Retrofit.Chatroom) {
            when (list.mails.size) {
                2 -> {
                    constTwo.visibility = View.VISIBLE
                    constThree.visibility = View.INVISIBLE
                    constFour.visibility = View.INVISIBLE
                }
                3 -> {
                    constTwo.visibility = View.INVISIBLE
                    constThree.visibility = View.VISIBLE
                    constFour.visibility = View.INVISIBLE
                }
                else -> {
                    constTwo.visibility = View.INVISIBLE
                    constThree.visibility = View.INVISIBLE
                    constFour.visibility = View.VISIBLE
                    binding.rvChatRoomFourText.text = "+${list.mails.size-3}"
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterChatRoom.ViewHolder, position: Int) {
        holder.bind(roomList[position])
    }

    override fun getItemCount() = roomList.size
}