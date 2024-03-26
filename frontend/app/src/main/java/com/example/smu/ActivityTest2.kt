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

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var open = false
        val url = BaseUrl.Socket_URL+"/ws/websocket"
        stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        val headers = listOf(StompHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDE5MTA5MTJAc2FuZ215dW5nLmtyIiwiaWF0IjoxNzExMzQ0NjA0LCJleHAiOjE3MTEzNDgyMDR9.irjamT0dFosS_XlePk8cI-lB1-CQzsUfHz0bcltk9kI"))
        stompClient.withServerHeartbeat(10000)
        stompClient.connect(headers)

        disposable = stompClient.topic("/topic/1234").subscribe()

        stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    open=true
                }
                LifecycleEvent.Type.CLOSED -> {
                    open=false
                }
                LifecycleEvent.Type.ERROR -> {
                    open=false
                }
                else->{

                }
            }
        }

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