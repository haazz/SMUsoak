package com.example.smu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.smu.databinding.ActivityTest2Binding
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ActivityTest2 : AppCompatActivity() {

    private val binding: ActivityTest2Binding by lazy { ActivityTest2Binding.inflate(layoutInflater) }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val url = "ws://ec2-43-200-30-120.ap-northeast-2.compute.amazonaws.com:8080/ws/websocket"
        val stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        val headers = listOf(StompHeader("Authorization", "1234"), StompHeader("authorization", "1234"))
        stompClient.withServerHeartbeat(10000)
        stompClient.withClientHeartbeat(10000)
        stompClient.connect(headers)

        stompClient.lifecycle().subscribe { lifecycleEvent ->
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
}