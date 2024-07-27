package com.example.smu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.databinding.RvUserListBinding

class AdapterDrawer(private val userNick: MutableList<String>, private val userMail: MutableList<String>, private val context: Context) : RecyclerView.Adapter<AdapterDrawer.ViewHolder>() {

    private val databaseHelper: DatabaseProfileImage by lazy{ DatabaseProfileImage.getInstance(context)}

    inner class ViewHolder(private val binding: RvUserListBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(userNick: String, userMail: String) {
            binding.rvUserListNick.text = userNick
            binding.rvUserListProfile.clipToOutline = true
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterDrawer.ViewHolder, position: Int) {
        holder.bind(userNick[position], userMail[position])
    }

    override fun getItemCount() = userNick.size
}