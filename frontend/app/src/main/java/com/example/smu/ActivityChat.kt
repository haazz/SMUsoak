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
import io.reactivex.schedulers.Schedulers
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
    private lateinit var chatconst: ConstraintLayout
    private lateinit var send: ImageButton
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatAdapter: AdapterChat
    private lateinit var plusbtn: ImageButton
    private lateinit var stompClient: StompClient
    private lateinit var disposable: Disposable
    private lateinit var roomId: String
    private lateinit var chatMessage: String
    private lateinit var chatList:MutableList<ChatMessage>
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
                chatconst.layoutParams.height = 40.dpToPx()
                chatEdit.layoutParams.height = 30.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatEdit.requestLayout()
            }else if(lines == 2){
                const.layoutParams.height = 70.dpToPx()
                chatconst.layoutParams.height = 60.dpToPx()
                chatEdit.layoutParams.height = 50.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatEdit.requestLayout()
            }else if(lines >= 3){
                const.layoutParams.height = 93.dpToPx()
                chatconst.layoutParams.height = 83.dpToPx()
                chatEdit.layoutParams.height = 73.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatEdit.requestLayout()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //이미지 불러오는 코드 웹소켓으로 할 수 있도록 수정 필요
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
                data.put("sender", sender)
                data.put("time", currentTime)
                stompClient.send("/topic/$roomId", data.toString())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                    {
                        chatList.add(ChatMessage("image $sender", imageText, currentTime))
                        databaseHelper.insertMessage(roomId,"image $sender",imageText,currentTime)
                        val position = recyclerViewChat.adapter?.itemCount?.minus(1) ?: 0
                        recyclerViewChat.adapter?.notifyItemInserted(position)
                    },
                    { throwable ->
                        Log.e("StompMessage", "Error sending message", throwable)
                    }
                )
                val position = recyclerViewChat.adapter?.itemCount?.minus(1) ?: 0
                recyclerViewChat.adapter?.notifyItemInserted(position)
            }
        }
    }

    // 메인 스레드 핸들러 생성
    private val mainHandler = Handler(Looper.getMainLooper())

    // Runnable을 정의하여 메인 스레드에서 실행할 작업 정의
    private val updateRecyclerViewRunnable = Runnable {
        val position = recyclerViewChat.adapter?.itemCount?.minus(1) ?: 0
        recyclerViewChat.adapter?.notifyItemChanged(position-1)
        recyclerViewChat.adapter?.notifyItemInserted(position)
        Log.d("chatlist",position.toString())
        recyclerViewChat.post {
            val layoutManager = recyclerViewChat.layoutManager as LinearLayoutManager
            val totalHeight = layoutManager.findViewByPosition(position+1)?.height ?: 0
            layoutManager.scrollToPositionWithOffset(position+1, totalHeight)
        }
    }

    //DataBase 가져오기
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
                    chatList.add(ChatMessage("system", currentDate, currentTime))
                    databaseHelper.insertMessage(roomId,"system",currentDate,currentTime)
                }else{
                    val lTime = chatList[chatList.size-1].time.split(" ")
                    val cTime = currentTime.split(" ")
                    if(lTime[0]!=cTime[0]) {
                        chatList.add(ChatMessage("system", currentDate, currentTime))
                        databaseHelper.insertMessage(roomId, "system", currentDate, currentTime)
                    }
                }

                chatList.add(ChatMessage(sender, message, time))
                databaseHelper.insertMessage(roomId,sender,message,time)

                if(chatList.size >= 2){
                    if(chatList[chatList.size-2].sender == chatList[chatList.size-1].sender && chatList[chatList.size-2].time == chatList[chatList.size-1].time){
                        chatList[chatList.size-2].time = ""
                    }
                }
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

        //리사이클러뷰 초기 설정
        recyclerViewChat = binding.chatRv
        recyclerViewChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chatAdapter = AdapterChat(chatList)
        recyclerViewChat.adapter = chatAdapter

        //변수 초기화
        plusbtn = binding.chatBtnPlus
        send = binding.chatBtnSend
        const = binding.chatMainConst
        chatconst = binding.chatConst
        chatEdit = binding.chatEdit
        chatEdit.addTextChangedListener(chatline)

        //보내기 버튼 눌렀을 때
        send.setOnClickListener{
            if(chatEdit.length() != 0){
                const.layoutParams.height = 50.dpToPx()
                chatconst.layoutParams.height = 40.dpToPx()
                chatEdit.layoutParams.height = 30.dpToPx()
                chatMessage=chatEdit.text.toString()

                const.requestLayout()
                chatconst.requestLayout()
                chatEdit.requestLayout()

                val currentTime = getCurrentTime()

                if(open){
                    val data = JSONObject()
                    data.put("message", chatMessage)
                    data.put("sender", sender)
                    data.put("time", currentTime)
                    stompClient.send("/topic/$roomId", data.toString()).subscribe()
                    chatEdit.setText("")
                }
            }
        }

        plusbtn.setOnClickListener {
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

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    //키보드가 열렸는지 확인
    private fun setupView() {
        // 키보드 Open/Close 체크 addOnGlobalLayoutListener을 통해서 레이아웃에 변화가 있을 때 마다 호출 됨
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

    //날짜를 yyyy년 mm월 dd일로 가져옴
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        return dateFormat.format(calendar.time)
    }

    //오전 or 오후 몇시인지 변환
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

    //Base64로 인코딩하기
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
            .setMessage("프로필 이미지를 설정하기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }
}