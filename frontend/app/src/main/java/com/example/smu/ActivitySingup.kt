package com.example.smu

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivitySingupBinding
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivitySingup : AppCompatActivity() {

    private lateinit var pwtext : TextView
    private lateinit var pwchecktext : TextView
    private lateinit var pwedit : EditText
    private lateinit var pwcheckedit : EditText
    private lateinit var btnsignup : Button
    private lateinit var idedit : EditText
    private lateinit var id : String
    private lateinit var pw : String

    //비밀번호 양식이 맞는치 체크
    private var pwcheck = false
    //학번 양식이 맞는지 체크
    private var idcheck = false

    //뒤로가기 버튼 누르면 로그인 화면으로 감
    override fun onBackPressed() {
        val intent = Intent(this, ActivityLogin::class.java)
        startActivity(intent)
        finish()
    }

    //비밀번호 일치하는지 확인
    private val pwcheckwatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if (inputText.isEmpty()) {
                pwchecktext.visibility = View.INVISIBLE
            } else {
                pwchecktext.visibility = View.VISIBLE
                if (inputText == pwedit.text.toString()) {
                    pwchecktext.visibility = View.INVISIBLE
                    pwcheck = true
                } else {
                    pwchecktext.text = "비밀번호가 일치하지 않습니다."
                    pwcheck = false
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //비밀번호 양식 확인
    private val pwwatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if (inputText.isEmpty()) {
                pwtext.visibility = View.INVISIBLE
            } else {
                pwtext.visibility = View.VISIBLE

                if (inputText.length in 6..20) {
                    pwtext.visibility = View.INVISIBLE
                } else {
                    pwtext.text = "비밀번호 형식이 올바르지 않습니다."
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //학번 양식 확인
    private val idwatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if(inputText.toIntOrNull() != null){
                idcheck=true
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val binding: ActivitySingupBinding by lazy { ActivitySingupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.signupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        idedit = binding.signupEditId
        pwedit = binding.signupEditPw
        pwtext = binding.signupTextPw
        pwcheckedit = binding.signupEditPwCheck
        pwchecktext = binding.signupTextPwcheck
        btnsignup = binding.signupBtnSignup

        pwedit.addTextChangedListener(pwwatcherListener)
        pwcheckedit.addTextChangedListener(pwcheckwatcherListener)
        idedit.addTextChangedListener(idwatcherListener)

        btnsignup.setOnClickListener {
            if(pwcheck && idedit.text.length == 9 && idcheck){
                id = idedit.text.toString()
                pw = pwedit.text.toString()
                val call = RetrofitObject.getRetrofitService.signup(Retrofit.Requestsignup(id+"@sangmyung.kr", pw))
                call.enqueue(object : Callback<Retrofit.Responsesignup> {
                    override fun onResponse(call: Call<Retrofit.Responsesignup>, response: Response<Retrofit.Responsesignup>) {
                        Log.d("Retrofit", "성공")
                        if (response.isSuccessful) {
                            val response = response.body()
                            if(response != null){
                                if(response.success){
                                    CustomDialog(id)
                                }
                            }
                        }
                        else{
                            Toast.makeText(this@ActivitySingup,"다시 시도해 주세요.",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Retrofit.Responsesignup>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit", errorMessage)
                    }
                })
            }else if(!pwcheck && idedit.text.length != 9){
                Toast.makeText(this, "회원가입 양식을 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }else if(idedit.text.length != 9 && pwcheck){
                Toast.makeText(this, "올바르지 않은 학번입니다.", Toast.LENGTH_SHORT).show()
            }else if(idedit.text.length == 9 && !pwcheck){
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }else if(idedit.text.length == 9 && !idcheck){
                Toast.makeText(this, "올바르지 않은 학번입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun CustomDialog(id: String) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(
            R.layout.dialog_signup_msg,
            findViewById(R.id.dialog_signup_layout)
        )

        // 다이얼로그 텍스트 설정
        builder.setView(view)

        val alertDialog = builder.create()
        view.findViewById<TextView>(R.id.dialog_singup_msg_text).text = id+"@sangmyung.kr(학교 메일)로 \n인증 메일이 발송되었습니다. \n인증 후 로그인이 가능합니다."
        alertDialog.setCanceledOnTouchOutside(false)
        view.findViewById<Button>(R.id.dialog_signup_msg_btn).setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        alertDialog.show()
    }

    //<-누르면 로그인 화면으로 넘어감
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, ActivityLogin::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}