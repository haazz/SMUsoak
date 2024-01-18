package com.example.smu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smu.databinding.RvChattingBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class AdapterChat(
    private val chatlist : MutableList<ChatList>,
    private val context: Context
) : RecyclerView.Adapter<AdapterChat.viewHolder>() {

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

        fun bind(list : ChatList) {
            if(list.user == 1){
                visibleMy()
                invisibleYour()
                binding.rvChattingDay.visibility = View.GONE
                binding.rvChattingMyimage.visibility = View.GONE
                binding.rvChattingMytext.text = list.text
                binding.rvChattingMyTime.text = list.time
            }
            else if(list.user == 0){
                visibleYour()
                invisibleMy()
                binding.rvChattingDay.visibility = View.GONE
                binding.rvChattingMyimage.visibility = View.GONE
                binding.rvChattingYourtext.text = list.text
                binding.rvChattingYourTime.text = list.time
            }else if(list.user == 2){
                binding.rvChattingDay.visibility = View.VISIBLE
                binding.rvChattingMyimage.visibility = View.GONE
                binding.rvChattingDayText.text = getCurrentDate()
                invisibleMy()
                invisibleYour()
            }else if(list.user == 4){
                binding.rvChattingDay.visibility = View.GONE
                binding.rvChattingMyimage.visibility = View.VISIBLE
                invisibleMy()
                invisibleYour()
                Glide.with(context)
                    .load(list.text)
                    .into(binding.rvChattingMyImage)
                binding.rvChattingMyImage.setOnClickListener {
                    val intent = Intent(context, ActivityImage::class.java)
                    intent.putExtra("image_uri", list.text)
                    context.startActivity(intent)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = RvChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterChat.viewHolder, position: Int) {
        holder.bind(chatlist[position])
    }

    override fun getItemCount() = chatlist.size

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        return dateFormat.format(calendar.time)
    }
}