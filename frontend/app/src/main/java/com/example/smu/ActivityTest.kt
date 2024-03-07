package com.example.smu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.Klaxon
import com.example.smu.databinding.ActivityTestBinding
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.ai.client.generativeai.Chat
import io.reactivex.disposables.Disposable

import okhttp3.OkHttpClient

class ActivityTest : AppCompatActivity() {

    private val binding: ActivityTestBinding by lazy { ActivityTestBinding.inflate(layoutInflater) }
    var stompConnection: Disposable? = null
    lateinit var topic: Disposable

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val url = "ws://ec2-43-200-30-120.ap-northeast-2.compute.amazonaws.com:8080/send"
        val intervalMillis = 1000L
        val client = OkHttpClient()

        val stomp = StompClient(client, intervalMillis)
        stomp.url=url

        val checktext = binding.testtext

        stompConnection = stomp.connect().subscribe {
            when (it.type) {

                Event.Type.OPENED -> {
                    checktext.text="연결되었습니다."
                    stomp.join("topic/1234").subscribe { stompMessage ->
                        val result = Klaxon().parse<Chat>(stompMessage)
                        if(result != null){
                            Log.d("web", result.toString())
                        }
                    }
                }
                Event.Type.CLOSED -> {}
                Event.Type.ERROR -> {}
                else -> {}
            }
        }

        topic=stomp.join("/topic/12345").subscribe{

        }
    }
}