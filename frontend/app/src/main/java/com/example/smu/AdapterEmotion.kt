package com.example.smu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smu.databinding.RvEmotionBinding
import com.example.smu.databinding.RvUserListBinding

class AdapterEmotion(private val emotionList: List<Int>,
                     private val onItemClick: (Int) -> Unit,
                     private val onDoubleClick: (Int) -> Unit) : RecyclerView.Adapter<AdapterEmotion.ViewHolder>(){

    inner class ViewHolder(private val binding: RvEmotionBinding) : RecyclerView.ViewHolder(binding.root){
        private var lastClickTime: Long = 0 // 마지막 클릭 시간
        private val handler = android.os.Handler() // 핸들러를 사용해 단일 클릭 지연 처리
        private var isDoubleClick = false

        fun bind(emotionDrawable: Int) {

            binding.rvEmotionImg.setImageResource(emotionDrawable)
            binding.root.setOnClickListener {
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastClickTime < 300) {
                    // 더블 클릭 감지
                    isDoubleClick = true
                    handler.removeCallbacksAndMessages(null) // 단일 클릭 콜백 제거
                    onDoubleClick(emotionDrawable) // 더블 클릭 이벤트 발생
                } else {
                    // 단일 클릭을 지연 실행
                    isDoubleClick = false
                    handler.postDelayed({
                        if (!isDoubleClick) {
                            onItemClick(emotionDrawable) // 단일 클릭 이벤트 발생
                        }
                    }, 300)
                }

                lastClickTime = currentTime // 마지막 클릭 시간 갱신
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvEmotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterEmotion.ViewHolder, position: Int) {
        holder.bind(emotionList[position])
    }

    override fun getItemCount() = emotionList.size
}