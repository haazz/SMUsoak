package com.example.smu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.smu.databinding.ActivityTest2Binding
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ActivityTest2 : AppCompatActivity() {

    private val binding: ActivityTest2Binding by lazy { ActivityTest2Binding.inflate(layoutInflater) }
    lateinit var stompClient: StompClient
    lateinit var disposable: Disposable
    private val user = MySharedPreference.user
    private val token= user.getString("token","")

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var open = false
        val url = BaseUrl.Socket_URL
        stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        val headers = listOf(StompHeader("Authorization", "Bearer $token"))
        stompClient.withServerHeartbeat(10000)
        stompClient.connect(headers)

        disposable = stompClient.topic("/topic/1234").subscribe()
//        이거 필요 없는데 연결 끊겼을 때 다시 확인할라면 필요함
//        stompClient.lifecycle().subscribe { lifecycleEvent ->
//            when (lifecycleEvent.type) {
//                LifecycleEvent.Type.OPENED -> {
//                    open=true
//                }
//                LifecycleEvent.Type.CLOSED -> {
//                    open=false
//                }
//                LifecycleEvent.Type.ERROR -> {
//                    open=false
//                }
//                else->{
//
//                }
//            }
//        }

        binding.sendtext.setOnClickListener {
            if(open){
                val text = binding.editexts.text.toString()
                val data = JSONObject()
                data.put("message", text)
                stompClient.send("/topic/1234", data.toString()).subscribe(
                    {
                        Log.d("StompMessage", "Message sent successfully: $text")
                    },
                    { throwable ->
                        Log.e("StompMessage", "Error sending message", throwable)
                    }
                )
            }
        }

        binding.cancel.setOnClickListener{
            disposable.dispose()
        }
    }

}