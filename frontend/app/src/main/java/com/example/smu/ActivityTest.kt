package com.example.smu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.smu.databinding.ActivityTestBinding
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import org.json.JSONObject
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient


class ActivityTest : AppCompatActivity() {

    private val binding: ActivityTestBinding by lazy { ActivityTestBinding.inflate(layoutInflater) }
    var stompConnection: Disposable? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val url = BaseUrl.BASE_URL+"/ws/websocket"
        val intervalMillis = 1000L
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val token = "201910911"
                val requestWithToken = originalRequest.newBuilder()
                    .header("Authorization", token)
                    .build()
                chain.proceed(requestWithToken)
            }
            .build()

        val stomp = StompClient(client, intervalMillis).apply { this@apply.url=url }
        val jsonObject = JSONObject()

        stompConnection = stomp.connect().subscribe ({
            when (it.type) {

                Event.Type.OPENED -> {
                    val topic = "/topic/1234"
                    binding.sendtext.setOnClickListener{

                        val text=binding.editexts.text.toString()
                        jsonObject.put("message", text)
                        val sendObservable = stomp.send(topic, jsonObject.toString())

                        sendObservable.subscribe { success ->
                            if (success) {
                                binding.send.text=text
                            } else {
                                binding.send.text="전송 실패"
                            }
                        }
                    }
                    val messageObservable = stomp.join(topic)
                    messageObservable.subscribe({it->
                        val text = JSONObject(it).getString("message")
                        runOnUiThread {
                            binding.receive.text = text
                        }
                    }, { error ->
                        // 구독 중 오류가 발생한 경우 처리
                        Log.e("StompConnections", "Error in subscription: ${error.message}", error)
                    })


                }
                Event.Type.CLOSED -> {
                    Log.d("StompConnections", "close")
                }
                Event.Type.ERROR -> {
                }
                else -> {}
            }
        },{ error ->
            Log.d("StompConnections", "Error: ${error.message ?: "Unknown error"}", error)
        })
    }
}