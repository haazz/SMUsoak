package com.example.smu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.databinding.RvUserListBinding

class AdapterEmotion(private val emotionList: MutableList<String>) : RecyclerView.Adapter<AdapterEmotion.ViewHolder>(){

    inner class ViewHolder(private val binding: RvUserListBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(emotionList: String) {

        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterEmotion.ViewHolder, position: Int) {
        holder.bind(emotionList[position])
    }

    override fun getItemCount() = emotionList.size
}