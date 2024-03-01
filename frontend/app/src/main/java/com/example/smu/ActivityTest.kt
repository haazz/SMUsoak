package com.example.smu

import android.app.usage.UsageEvents
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.smu.databinding.ActivityTestBinding
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.OkHttpClient
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class ActivityTest : AppCompatActivity() {

    private val binding: ActivityTestBinding by lazy { ActivityTestBinding.inflate(layoutInflater) }
    lateinit var stompConnection: Disposable
    lateinit var topic: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val url = "ws://ec2-43-200-30-120.ap-northeast-2.compute.amazonaws.com:8080/send"
        val intervalMillis = 1000L
        val client = OkHttpClient()

        val stomp = StompClient(client, intervalMillis)

        stompConnection = stomp.connect().lifecycle().subscribe {
            when (it.type) {
                LifecycleEvent.Type.OPENED -> {

                }
                LifecycleEvent.Type.CLOSED -> {

                }
                LifecycleEvent.Type.ERROR -> {

                }
            }
        }

        // subscribe
        topic = stomp.subscribe("/destination").subscribe { Log.i(TAG, it) }

// unsubscribe
        topic.dispose()

// send
        stomp.send("/destination", "dummy message").subscribe {
            if (it) {
            }
        }

// disconnect
        stompConnection.dispose()
    }

    fun sendMessageToServer(message: String) {
        // STOMP 클라이언트를 사용하여 서버로 메시지 전송
        stomp.send("/destination", message).subscribe { success ->
            if (success) {
                // 성공적으로 메시지를 보냈을 때의 동작
                Log.d("테이스", "메시지 전송 성공")
            } else {
                // 메시지 전송 실패 시의 동작
                Log.e("테스트", "메시지 전송 실패")
            }
        }
    }
}