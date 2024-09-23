package com.example.smu

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivityChatBinding
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
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
    private lateinit var roomId: String
    private lateinit var chatMessage: String
    private lateinit var chatList: MutableList<ChatMessage>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerView: View
    private lateinit var menu: ImageButton
    private lateinit var drawerRecyclerView: RecyclerView
    private lateinit var drawerAdapter: AdapterDrawer
    private lateinit var userNick: MutableList<String>
    private lateinit var userMail: MutableList<String>
    private lateinit var imagePart: MultipartBody.Part
    private lateinit var mediaType: MediaType
    private val compositeDisposable = CompositeDisposable()
    private var open = false
    private val user = MySharedPreference.user
    private val sender = user.getString("mail","")
    private val token= user.getString("accessToken","")
    private val headers = listOf(StompHeader("Authorization", "Bearer $token"))
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
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = getRealPathFromUri(uri)
            val file = File(path!!)
            val json = JSONObject()
            json.put("roomId", roomId)
            val room = json.toString().toRequestBody("application/json".toMediaType())
            val currentTime = getCurrentTime()

            //jpg, jpeg, png 인지 확인
            if(file.toString().endsWith("jpg") || file.toString().endsWith("jpeg")){
                mediaType = "image/jpeg".toMediaType()
            }else if(file.toString().endsWith("png")){
                mediaType = "image/png".toMediaType()
            }

            val imageRequestBody = file.asRequestBody(mediaType)
            imagePart = MultipartBody.Part.createFormData("file", file.name, imageRequestBody)

            Log.d("이미지 추적 : url다운 요청", LocalDateTime.now().toString())
            val call = RetrofitObject.getRetrofitService.chatImage("Bearer $token", imagePart, room)
            call.enqueue(object : Callback<Retrofit.ResponseChatImage> {
                override fun onResponse(call: Call<Retrofit.ResponseChatImage>, response: Response<Retrofit.ResponseChatImage>) {
                    if (response.isSuccessful) {
                        val chatImageResponse = response.body()
                        if (chatImageResponse != null && chatImageResponse.success) {
                            val imageUrl = chatImageResponse.data.downloadUrl
                            if(open) {
                                Log.d("chatting", chatImageResponse.toString())
                                val data = JSONObject()
                                data.put("roomId", roomId)
                                data.put("message", imageUrl)
                                data.put("senderMail", "$sender")
                                data.put("time", currentTime)
                                data.put("flag", 1)
                                stompClient.send("/app/send", data.toString()).subscribe()
                                Log.d("이미지 추적 : 이미지 url 다운 및 url 송신", LocalDateTime.now().toString())
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseChatImage>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }
    }

    // 메인 스레드 핸들러 생성
    private val mainHandler = Handler(Looper.getMainLooper())

    // Main Tread 에서 할 코드임
    @SuppressLint("NotifyDataSetChanged")
    private val updateRecyclerViewRunnable = Runnable {
        recyclerViewChat.adapter?.notifyDataSetChanged()

        recyclerViewChat.post {
            val layoutManager = recyclerViewChat.layoutManager as LinearLayoutManager
            val pos = chatAdapter.itemCount - 1
            val totalHeight = layoutManager.findViewByPosition(pos)?.height ?: 0
            layoutManager.scrollToPositionWithOffset(pos, totalHeight)
        }
    }

    //DataBase 가져옴
    private val databaseChat: DatabaseChat by lazy{ DatabaseChat.getInstance(applicationContext) }
    private val databaseImage: DatabaseChatImage by lazy { DatabaseChatImage.getInstance(applicationContext) }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        drawerLayout = binding.chatDrawLayout
        drawerView = binding.chatDrawer
        menu = binding.chatBtnMenu

        userNick = intent.getStringArrayListExtra("userNick")!!
        userMail = intent.getStringArrayListExtra("userMail")!!
        Log.d("리스트", userNick.toString()+"  "+userMail.toString())

        drawerRecyclerView = findViewById(R.id.chat_drawer_rv)
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        drawerAdapter = AdapterDrawer(userNick, userMail, this)
        drawerRecyclerView.adapter = drawerAdapter

        setupView() //키보드 열린지 체크

        val url = BaseUrl.SOCKET_URL

        stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        stompClient.withServerHeartbeat(10000)
        stompClient.connect(headers)
        roomId = intent.getStringExtra("roomId")!!

        val topicDisposable = stompClient.topic("/topic/$roomId").subscribe(
            { topicMessage ->
                Log.d("이미지 추적 : 메시지 수신", LocalDateTime.now().toString())
                val payload = topicMessage.payload
                val jsonObject = JSONObject(payload)
                val roomId = jsonObject.getString("roomId")
                val message = jsonObject.getString("message")
                val sender = jsonObject.getString("senderMail")
                val time = jsonObject.getString("time")
                val flag = jsonObject.getInt("flag")

                val currentTime=getCurrentTime()
                val currentDate=getCurrentDate()


                if(chatList.size == 0) {
                    chatList.add(ChatMessage("system", currentDate, currentTime, 3))
                    databaseChat.insertMessage(roomId,"system",currentDate,currentTime, 3)
                }else{
                    val lTime = chatList[chatList.size-1].time.split(" ")
                    val cTime = currentTime.split(" ")
                    if(lTime[0]!=cTime[0]) {
                        chatList.add(ChatMessage("system", currentDate, currentTime, 3))
                        databaseChat.insertMessage(roomId, "system", currentDate, currentTime, 3)
                    }
                }

                // flag 앞이 0이면 문자 1이면 이미지
                if(chatList[chatList.size-1].sender == sender){ // 같은 사용자가 연속으로 보낼 때
                    if(flag == 0){
                        chatList.add(ChatMessage(sender, message, time, 2))
                        databaseChat.insertMessage(roomId,sender,message,time, 2)
                    }else{
                        chatList.add(ChatMessage(sender, message, time, 12))
                        databaseChat.insertMessage(roomId,sender,message,time, 12)
                    }
                }else{ // 다른 사용자가 보낼 때
                    if(flag == 0){
                        chatList.add(ChatMessage(sender, message, time, 0))
                        databaseChat.insertMessage(roomId,sender,message,time, 0)
                    }else{
                        chatList.add(ChatMessage(sender, message, time, 10))
                        databaseChat.insertMessage(roomId,sender,message,time, 10)
                    }
                }

                mainHandler.post(updateRecyclerViewRunnable)
            },
            { throwable ->
                Log.e("stomp", "Error while receiving message", throwable)
            }
        )

        compositeDisposable.add(topicDisposable)

        val lifecycleDisposable = stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    open=true
                }
                LifecycleEvent.Type.CLOSED -> {
                    open=false
                    stompClient.connect(headers)
                }
                LifecycleEvent.Type.ERROR -> {
                    open=false
                    stompClient.connect(headers)
                }
                else->{
                }
            }
        }

        compositeDisposable.add(lifecycleDisposable)

        chatList=databaseChat.getAllMessages(roomId)
        Log.d("chatList", chatList.toString())

        //recyclerView 초기 설정
        recyclerViewChat = binding.chatRv
        recyclerViewChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chatAdapter = AdapterChat(chatList, this)
        recyclerViewChat.adapter = chatAdapter

        //변수 초기화
        plusBtn = binding.chatBtnPlus
        send = binding.chatBtnSend
        const = binding.chatMainConst
        chatConst = binding.chatConst
        chatEdit = binding.chatEdit
        chatEdit.addTextChangedListener(chatline)

        //서랍 열기
        menu.setOnClickListener {
            drawerLayout.openDrawer(drawerView)
        }

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

                if(open) {
                    val data = JSONObject()
                    data.put("roomId", roomId)
                    data.put("message", chatMessage)
                    data.put("senderMail", "$sender")
                    data.put("time", currentTime)
                    data.put("flag", 0)
                    stompClient.send("/app/send", data.toString()).subscribe()
                    chatEdit.text = null
                }
            }
        }

        plusBtn.setOnClickListener {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    // Android 13 이상
                    if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        requestPermissions(arrayOf(READ_MEDIA_IMAGES), 1000)
                    }
                }
                else -> {
                    // Android 12 이하
                    if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 1000)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        stompClient.disconnect()
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    //KeyBoard open/close 확인
    private fun setupView() {
        // Layout 변화가 있을 때 마다 호출 됨
        drawerLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            drawerLayout.getWindowVisibleDisplayFrame(rect)

            val rootViewHeight = drawerLayout.rootView.height
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
}