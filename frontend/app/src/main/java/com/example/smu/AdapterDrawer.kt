package com.example.smu

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smu.databinding.RvUserListBinding

class AdapterDrawer(private val userNick: MutableList<String>, private val userMail: MutableList<String>, private val context: Context) : RecyclerView.Adapter<AdapterDrawer.ViewHolder>() {

    private val databaseHelper: DatabaseProfileImage by lazy{ DatabaseProfileImage.getInstance(context)}

    inner class ViewHolder(private val binding: RvUserListBinding) : RecyclerView.ViewHolder(binding.root){

        private val profile = binding.rvUserListProfile

        fun bind(userNick: String, userMail: String) {
            binding.rvUserListNick.text = userNick
            profile.clipToOutline = true
            Glide.with(context)
                .load(databaseHelper.getImage(userMail))
                .placeholder(R.drawable.svg_profile) // 로딩 중일 때 보여줄 이미지
                .error(R.drawable.svg_profile)             // 로딩 실패 시 보여줄 이미지
                .into(profile)
            profile.setOnClickListener {
                val intent = Intent(context, ActivityImage::class.java)
                intent.putExtra("image_uri", databaseHelper.getImage(userMail))
                context.startActivity(intent)
            }
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