package com.example.smu

import DatabaseChat
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.databinding.ActivityChatBinding
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

class ActivityChat : AppCompatActivity() {

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }

    private lateinit var const: ConstraintLayout
    private lateinit var chatEdit: EditText
    private lateinit var chatConst: ConstraintLayout
    private lateinit var send: ImageButton
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatAdapter: AdapterChat
    private lateinit var plusBtn: ImageButton
    private lateinit var stompClient: StompClient
    private lateinit var disposable: Disposable
    private lateinit var roomId: String
    private lateinit var chatMessage: String
    private lateinit var chatList:MutableList<ChatMessage>
    private var chatId = 0
    private var open = false
    private val user = MySharedPreference.user
    private val sender = user.getString("mail","")
    private val token= user.getString("token","")
    private var isKeyboardOpened = false

    //채팅 줄 수가 늘 때 editText 크기도 같이 변동 되도록 해주는 textWatcher
    private val chatline = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val lines = chatEdit.lineCount
            if(lines == 1){
                const.layoutParams.height = 50.dpToPx()
                chatConst.layoutParams.height = 40.dpToPx()
                chatEdit.layoutParams.height = 30.dpToPx()

                const.requestLayout()
                chatConst.requestLayout()
                chatEdit.requestLayout()
            }else if(lines == 2){
                const.layoutParams.height = 70.dpToPx()
                chatConst.layoutParams.height = 60.dpToPx()
                chatEdit.layoutParams.height = 50.dpToPx()

                const.requestLayout()
                chatConst.requestLayout()
                chatEdit.requestLayout()
            }else if(lines >= 3){
                const.layoutParams.height = 93.dpToPx()
                chatConst.layoutParams.height = 83.dpToPx()
                chatEdit.layoutParams.height = 73.dpToPx()

                const.requestLayout()
                chatConst.requestLayout()
                chatEdit.requestLayout()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //이미지 전송
    @SuppressLint("CheckResult")
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = getRealPathFromUri(uri)
            val file = File(path!!)
            var imageText =""
            //jpg, jpeg, png 인지 확인
            if(file.toString().substring(file.toString().length-3)=="jpg" || file.toString().substring(file.toString().length-4)=="jpeg"){
                imageText = encodeImageToBase64(path, "jpeg")!!
            }else if(file.toString().substring(file.toString().length-3)=="png"){
                imageText = encodeImageToBase64(path, "PNG")!!
            }
            if(open){
                val data = JSONObject()
                val currentTime = getCurrentTime()
                data.put("message", imageText)
                data.put("sender", "image $sender")
                data.put("time", currentTime)
                stompClient.send("/topic/$roomId", data.toString()).subscribe()
            }
        }
    }

    // 메인 스레드 핸들러 생성
    private val mainHandler = Handler(Looper.getMainLooper())

    // Main Tread 에서 할 코드임
    private val updateRecyclerViewRunnable = Runnable {
        val position = recyclerViewChat.adapter?.itemCount?.minus(1) ?: 0
        recyclerViewChat.adapter?.notifyItemChanged(position-1)
        recyclerViewChat.adapter?.notifyItemInserted(position)
        recyclerViewChat.post {
            val layoutManager = recyclerViewChat.layoutManager as LinearLayoutManager
            val pos = chatAdapter.itemCount - 1
            val totalHeight = layoutManager.findViewByPosition(pos)?.height ?: 0
            layoutManager.scrollToPositionWithOffset(pos, totalHeight)
        }
    }

    //DataBase 가져옴
    private val databaseHelper: DatabaseChat by lazy{ DatabaseChat.getInstance(applicationContext)}

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView() //키보드 열린지 체크

        val url = BaseUrl.Socket_URL

        stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        val headers = listOf(StompHeader("Authorization", "Bearer $token"))
        stompClient.withServerHeartbeat(10000)
        stompClient.connect(headers)
        roomId = "2"
        disposable = stompClient.topic("/topic/$roomId").subscribe(//임시 방번호
            { topicMessage ->
                val payload = topicMessage.payload
                val jsonObject = JSONObject(payload)
                val message = jsonObject.getString("message")
                val sender = jsonObject.getString("sender")
                val time = jsonObject.getString("time")

                val currentTime=getCurrentTime()
                val currentDate=getCurrentDate()

                if(chatList.size == 0) {
                    chatList.add(ChatMessage("system", currentDate, currentTime, chatId))
                    databaseHelper.insertMessage(roomId,"system",currentDate,currentTime, chatId)
                    chatId+=1
                }else{
                    val lTime = chatList[chatList.size-1].time.split(" ")
                    val cTime = currentTime.split(" ")
                    if(lTime[0]!=cTime[0]) {
                        chatList.add(ChatMessage("system", currentDate, currentTime, chatId))
                        databaseHelper.insertMessage(roomId, "system", currentDate, currentTime, chatId)
                        chatId+=1
                    }
                }

                if(chatList.size >= 2){
                    if(chatList[chatList.size-1].sender.split(" ")[1] == sender.split(" ")[1] && chatList[chatList.size-1].time == time){
                        chatList[chatList.size-1].time = ""
                        databaseHelper.updateMessage(roomId,chatId-1)
                    }
                }

                chatList.add(ChatMessage(sender, message, time, chatId))
                databaseHelper.insertMessage(roomId,sender,message,time,chatId)

                chatId+=1
                mainHandler.post(updateRecyclerViewRunnable)
            },
            { throwable ->
                Log.e("stomp", "Error while receiving message", throwable)
            }
        )

        stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    open=true
                }
                LifecycleEvent.Type.CLOSED -> {
                    Toast.makeText(this@ActivityChat,"재연결 시도 중 입니다.", Toast.LENGTH_SHORT).show()
                    stompClient.connect(headers)
                    open=false
                }
                LifecycleEvent.Type.ERROR -> {
                    Toast.makeText(this@ActivityChat,"재연결 시도 중 입니다.", Toast.LENGTH_SHORT).show()
                    stompClient.connect(headers)
                    open=false
                }
                else->{

                }
            }
        }
        chatList=databaseHelper.getAllMessages(roomId)
        if(chatList.isNotEmpty()){
            chatId=chatList[chatList.size-1].chatId+1
        }

        //recyclerView 초기 설정
        recyclerViewChat = binding.chatRv
        recyclerViewChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chatAdapter = AdapterChat(chatList)
        recyclerViewChat.adapter = chatAdapter

        //변수 초기화
        plusBtn = binding.chatBtnPlus
        send = binding.chatBtnSend
        const = binding.chatMainConst
        chatConst = binding.chatConst
        chatEdit = binding.chatEdit
        chatEdit.addTextChangedListener(chatline)

        //보내기 버튼 눌렀을 때
        send.setOnClickListener{
            if(chatEdit.length() != 0){
                const.layoutParams.height = 50.dpToPx()
                chatConst.layoutParams.height = 40.dpToPx()
                chatEdit.layoutParams.height = 30.dpToPx()
                chatMessage=chatEdit.text.toString()

                const.requestLayout()
                chatConst.requestLayout()
                chatEdit.requestLayout()

                val currentTime = getCurrentTime()

                if(open){
                    val data = JSONObject()
                    data.put("message", chatMessage)
                    data.put("sender", "message $sender")
                    data.put("time", currentTime)
                    stompClient.send("/topic/$roomId", data.toString()).subscribe()
                    chatEdit.setText("")
                }
            }
        }

        plusBtn.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    //KeyBoard open/close 확인
    private fun setupView() {
        // Layout 변화가 있을 때 마다 호출 됨
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

    //날짜를 yyyy 년 mm 월 dd 일로 가져옴
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        return dateFormat.format(calendar.time)
    }

    //오전 or 오후 몇 시인지 변환
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd a hh:mm")
        return dateFormat.format(calendar.time)
    }

    //이미지 저장 주소 가져오기
    private fun getRealPathFromUri(uri: Uri): String? {
        val context = applicationContext
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    //Base64로 InCoding
    private fun encodeImageToBase64(imagePath: String, type: String): String? {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        val outputStream = ByteArrayOutputStream()
        if(type=="PNG"){
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }else{
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    //권한 요청
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("이미지를 전송하기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }
}
