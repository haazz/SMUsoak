package com.example.smu

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.smu.databinding.ActivityChatBinding
import java.lang.Double.min
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.min

class ActivityChat : AppCompatActivity() {

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private lateinit var const: ConstraintLayout
    private lateinit var chatedit: EditText
    private lateinit var chatconst: ConstraintLayout
    private lateinit var send: ImageButton
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatAdapter: AdapterChat
    private lateinit var plusbtn: ImageButton
    private var chatlist = mutableListOf<ChatList>()
    private var isKeyboardOpened = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val chatline = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val lines = chatedit.lineCount
            if(lines == 1){
                const.layoutParams.height = 50.dpToPx()
                chatconst.layoutParams.height = 40.dpToPx()
                chatedit.layoutParams.height = 30.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatedit.requestLayout()
            }else if(lines == 2){
                const.layoutParams.height = 70.dpToPx()
                chatconst.layoutParams.height = 60.dpToPx()
                chatedit.layoutParams.height = 50.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatedit.requestLayout()
            }else if(lines >= 3){
                const.layoutParams.height = 93.dpToPx()
                chatconst.layoutParams.height = 83.dpToPx()
                chatedit.layoutParams.height = 73.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatedit.requestLayout()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            chatlist.add(ChatList(uri.toString(), "", 4))
            recyclerViewChat.adapter?.notifyDataSetChanged()
            recyclerViewChat.post {
                val layoutManager = recyclerViewChat.layoutManager as LinearLayoutManager
                val position = chatAdapter.itemCount - 1
                val totalHeight = layoutManager.findViewByPosition(position)?.height ?: 0
                layoutManager.scrollToPositionWithOffset(position, totalHeight)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()

        sharedPreferences = getSharedPreferences("Time", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        recyclerViewChat = binding.chatRv
        recyclerViewChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chatAdapter = AdapterChat(chatlist, this)
        recyclerViewChat.adapter = chatAdapter

        plusbtn = binding.chatBtnPlus
        send = binding.chatBtnSend
        const = binding.chatMainConst
        chatconst = binding.chatConst
        chatedit = binding.chatEdit
        chatedit.addTextChangedListener(chatline)

        if(chatlist.size == 0){
            chatlist.add(ChatList("", "", 2))
            editor.putString("day", getCurrentDate())
            editor.apply()
        }

        send.setOnClickListener{
            if(chatedit.length() != 0){
                const.layoutParams.height = 50.dpToPx()
                chatconst.layoutParams.height = 40.dpToPx()
                chatedit.layoutParams.height = 30.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatedit.requestLayout()

                if(sharedPreferences.getString("day", "") != getCurrentDate()){
                    chatlist.add(ChatList("", "", 2))
                    editor.putString("day", getCurrentDate())
                    editor.apply()
                }

                val currentTime = getCurrentTime()
                chatlist.add(ChatList(chatedit.text.toString(), currentTime, 1))

                if(chatlist.size >= 2){
                    if(chatlist[chatlist.size-2].type == chatlist[chatlist.size-1].type && chatlist[chatlist.size-2].time == chatlist[chatlist.size-1].time){
                        chatlist[chatlist.size-2].time = ""
                    }
                }

                recyclerViewChat.adapter?.notifyDataSetChanged()
                Log.d("chat", chatAdapter.itemCount.toString())
                recyclerViewChat.post {
                    val layoutManager = recyclerViewChat.layoutManager as LinearLayoutManager
                    val position = chatAdapter.itemCount - 1
                    val totalHeight = layoutManager.findViewByPosition(position)?.height ?: 0
                    layoutManager.scrollToPositionWithOffset(position, totalHeight)
                }
                chatedit.setText("")
            }
        }

        plusbtn.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("a hh:mm")
        return dateFormat.format(calendar.time)
    }

    private fun setupView() {
        // 키보드 Open/Close 체크
        binding.chatLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.chatLayout.getWindowVisibleDisplayFrame(rect)

            val rootViewHeight = binding.chatLayout.rootView.height
            val heightDiff = rootViewHeight - rect.height()
            val isOpen = heightDiff > rootViewHeight * 0.25 // true == 키보드 올라감

            if (isOpen && !isKeyboardOpened) {
                recyclerViewChat.post {
                    recyclerViewChat.scrollToPosition(chatAdapter.itemCount - 1)
                }
                isKeyboardOpened = true
            } else if (!isOpen && isKeyboardOpened) {
                isKeyboardOpened = false
            }
        }
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        return dateFormat.format(calendar.time)
    }
}