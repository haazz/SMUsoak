package com.example.smu

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smu.databinding.RvChattingBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class AdapterChat(
    private val chatList : MutableList<ChatMessage>,
    private val context: Context
) : RecyclerView.Adapter<AdapterChat.viewHolder>() {

    private val user = MySharedPreference.user
    private val sender = user.getString("mail","")

    inner class viewHolder(private val binding: RvChattingBinding) : RecyclerView.ViewHolder(binding.root){
        fun visibleMy(){
            binding.rvChattingMychatConst.visibility = View.VISIBLE
            binding.rvChattingMyTime.visibility = View.VISIBLE
        }

        fun visibleYour(){
            binding.rvChattingYourchatConst.visibility = View.VISIBLE
            binding.rvChattingYourTime.visibility = View.VISIBLE
        }

        fun invisibleYour(){
            binding.rvChattingYourchatConst.visibility = View.INVISIBLE
            binding.rvChattingYourTime.visibility = View.INVISIBLE
        }

        fun invisibleMy(){
            binding.rvChattingMychatConst.visibility = View.INVISIBLE
            binding.rvChattingMyTime.visibility = View.INVISIBLE
        }

        fun bind(list : ChatMessage) {
            if(list.sender == sender){
                visibleMy()
                invisibleYour()
                binding.rvChattingDay.visibility = View.GONE
                binding.rvChattingMyimage.visibility = View.GONE
                binding.rvChattingMytext.text = list.message
                binding.rvChattingMyTime.text = list.time.substring(9)
            }
            else{
                visibleYour()
                invisibleMy()
                binding.rvChattingDay.visibility = View.GONE
                binding.rvChattingMyimage.visibility = View.GONE
                binding.rvChattingYourtext.text = list.message
                binding.rvChattingYourTime.text = list.time.substring(9)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = RvChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterChat.viewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount() = chatList.size

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        return dateFormat.format(calendar.time)
    }
}