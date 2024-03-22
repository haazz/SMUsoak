package com.example.smu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.smu.databinding.ActivityTest2Binding
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ActivityTest2 : AppCompatActivity() {

    private val binding: ActivityTest2Binding by lazy { ActivityTest2Binding.inflate(layoutInflater) }
    lateinit var topic: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val url = BaseUrl.BASE_URL+"/ws/websocket"
        val stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        val headers = listOf(StompHeader("Authorization", "1234"), StompHeader("authorization", "1234"))
        stompClient.withServerHeartbeat(10000)
        stompClient.withClientHeartbeat(10000)
        stompClient.connect(headers)

        topic = stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("StompConnections", "open")
                }
                LifecycleEvent.Type.CLOSED -> {
                }
                LifecycleEvent.Type.ERROR -> {
                }
                else->{

                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        topic.dispose() // Disposable 객체를 dispose
    }
}