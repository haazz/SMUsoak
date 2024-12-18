package com.example.smu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.databinding.RvEmotionBinding
import com.example.smu.databinding.RvUserListBinding

class AdapterEmotion(private val emotionList: List<Int>) : RecyclerView.Adapter<AdapterEmotion.ViewHolder>(){

    inner class ViewHolder(private val binding: RvEmotionBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(emotionDrawable: Int) {
            binding.rvEmotionImg.setImageResource(emotionDrawable)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvEmotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterEmotion.ViewHolder, position: Int) {
        holder.bind(emotionList[position])
    }

    override fun getItemCount() = emotionList.size
}