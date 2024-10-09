package com.example.smu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smu.connection.Retrofit
import com.example.smu.databinding.RvChatRoomBinding

class AdapterChatRoom(private val roomList : MutableList<Retrofit.Chatroom>,
                      private val context: Context,
                      private val userNick: HashMap<Int, MutableList<String>>) : RecyclerView.Adapter<AdapterChatRoom.ViewHolder>() {

    private val databaseHelper: DatabaseProfileImage by lazy{ DatabaseProfileImage.getInstance(context)}

    inner class ViewHolder(private val binding: RvChatRoomBinding) : RecyclerView.ViewHolder(binding.root){

        private val constTwo = binding.rvChatRoomConstTwo
        private val constThree = binding.rvChatRoomConstThree
        private val constFour = binding.rvChatRoomConstFour
        private val profile = binding.rvChatRoomProfile
        private val user = Application.user

        @SuppressLint("SetTextI18n")
        fun bind(list : Retrofit.Chatroom) {
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, ActivityChat::class.java)
                intent.putExtra("roomId", list.roomId.toString())
                intent.putExtra("userNick", ArrayList(userNick[list.roomId]!!))
                intent.putExtra("userMail", ArrayList(list.mails))
                Log.d("roomId adapter", list.roomId.toString())
                binding.root.context.startActivity(intent)
            }

            binding.rvChatRoomTitle.text = list.roomId.toString()

            when (list.mails.size) {
                2 -> {
                    profile.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[0]))
                        .into(profile)
                    profile.clipToOutline = true
                    constTwo.visibility = View.INVISIBLE
                    constThree.visibility = View.INVISIBLE
                    constFour.visibility = View.INVISIBLE
                }
                3 -> {
                    profile.visibility = View.INVISIBLE
                    constTwo.visibility = View.VISIBLE
                    constThree.visibility = View.INVISIBLE
                    constFour.visibility = View.INVISIBLE
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[0]))
                        .into(binding.rvChatRoomTwo1)
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[1]))
                        .into(binding.rvChatRoomTwo2)
                    binding.rvChatRoomTwo1.clipToOutline = true
                    binding.rvChatRoomTwo2.clipToOutline = true
                    databaseHelper.getImage(list.mails[0])
                    databaseHelper.getImage(list.mails[1])
                }
                4 -> {
                    profile.visibility = View.INVISIBLE
                    constTwo.visibility = View.INVISIBLE
                    constThree.visibility = View.VISIBLE
                    constFour.visibility = View.INVISIBLE
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[0]))
                        .into(binding.rvChatRoomThree1)
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[1]))
                        .into(binding.rvChatRoomThree2)
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[2]))
                        .into(binding.rvChatRoomThree3)
                    binding.rvChatRoomThree1.clipToOutline = true
                    binding.rvChatRoomThree2.clipToOutline = true
                    binding.rvChatRoomThree3.clipToOutline = true
                }
                else -> {
                    profile.visibility = View.INVISIBLE
                    constTwo.visibility = View.INVISIBLE
                    constThree.visibility = View.INVISIBLE
                    constFour.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[0]))
                        .into(binding.rvChatRoomFour1)
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[1]))
                        .into(binding.rvChatRoomFour1)
                    Glide.with(context)
                        .load(databaseHelper.getImage(list.mails[2]))
                        .into(binding.rvChatRoomFour1)
                    binding.rvChatRoomFour1.clipToOutline = true
                    binding.rvChatRoomFour2.clipToOutline = true
                    binding.rvChatRoomFour3.clipToOutline = true
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