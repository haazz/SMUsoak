package com.example.smu

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.smu.databinding.ActivityChatBinding

class ActivityChat : AppCompatActivity() {

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private lateinit var const: ConstraintLayout
    private lateinit var chatedit: EditText
    private lateinit var chatconst: ConstraintLayout
    private lateinit var send: ImageButton

    private val chatline = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val lines = chatedit.lineCount
            if(lines == 1){
                const.layoutParams.height = 40.dpToPx()
                chatconst.layoutParams.height = 30.dpToPx()
                chatedit.layoutParams.height = 20.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatedit.requestLayout()
            }else if(lines == 2){
                const.layoutParams.height = 60.dpToPx()
                chatconst.layoutParams.height = 50.dpToPx()
                chatedit.layoutParams.height = 45.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatedit.requestLayout()
            }else if(lines >= 3){
                const.layoutParams.height = 92.dpToPx()
                chatconst.layoutParams.height = 72.dpToPx()
                chatedit.layoutParams.height = 67.dpToPx()

                const.requestLayout()
                chatconst.requestLayout()
                chatedit.requestLayout()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        send = binding.chatBtnSend
        const = binding.chatMainConst
        chatconst = binding.chatConst
        chatedit = binding.chatEdit
        chatedit.addTextChangedListener(chatline)

        send.setOnClickListener{
            const.layoutParams.height = 40.dpToPx()
            chatconst.layoutParams.height = 30.dpToPx()
            chatedit.layoutParams.height = 20.dpToPx()

            const.requestLayout()
            chatconst.requestLayout()
            chatedit.requestLayout()

            chatedit.setText("")
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}