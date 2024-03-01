package com.example.smu

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivitySingupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivitySingup : AppCompatActivity() {

    private lateinit var text_pw : TextView
    private lateinit var text_pwcheck : TextView
    private lateinit var edit_pw : EditText
    private lateinit var edit_pwcheck : EditText
    private lateinit var edit_checknum : EditText
    private lateinit var edit_id : EditText
    private lateinit var btn_signup : Button
    private lateinit var btn_sendnum : Button
    private lateinit var btn_checknum : Button

    private lateinit var id : String
    private lateinit var pw : String
    private lateinit var email: String

    //비밀번호 양식이 맞는치 체크
    private var pwcheck = false
    //학번 양식이 맞는지 체크
    private var idcheck = false
    //인증 번호가 전달 되었는지 체크
    private var sendnumcheck = false
    //이메일 인증이 되었는지 체크
    private  var emailcheck = false

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
                text_pwcheck.visibility = View.INVISIBLE
            } else {
                text_pwcheck.visibility = View.VISIBLE
                if (inputText == edit_pw.text.toString()) {
                    text_pwcheck.visibility = View.INVISIBLE
                    pwcheck = true
                } else {
                    text_pwcheck.text = "비밀번호가 일치하지 않습니다."
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
                text_pw.visibility = View.INVISIBLE
            } else {
                text_pw.visibility = View.VISIBLE

                if (inputText.length in 6..20) {
                    text_pw.visibility = View.INVISIBLE
                } else {
                    text_pw.text = "비밀번호 형식이 올바르지 않습니다."
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
            idcheck = inputText.toIntOrNull() != null
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //인증 번호 입력 확인
    private val numwatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if (inputText.length==6 && sendnumcheck){
                btn_checknum.isEnabled = true
                btn_checknum.alpha = 1f
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

        edit_id = binding.signupEditId
        edit_pw = binding.signupEditPw
        edit_pwcheck = binding.signupEditPwCheck
        edit_checknum = binding.signupEditEmailnum
        text_pw = binding.signupTextPw
        text_pwcheck = binding.signupTextPwcheck
        btn_signup = binding.signupBtnNext
        btn_sendnum = binding.signupBtnSendnum
        btn_checknum = binding.signupBtnChecknum

        edit_pw.addTextChangedListener(pwwatcherListener)
        edit_pwcheck.addTextChangedListener(pwcheckwatcherListener)
        edit_id.addTextChangedListener(idwatcherListener)
        edit_checknum.addTextChangedListener(numwatcherListener)

        //인증번호 전송 버튼
        btn_sendnum.setOnClickListener {
            Toast.makeText(this@ActivitySingup, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show()
            btn_sendnum.isEnabled = false
            if (idcheck && edit_id.text.length==9) {
                id = edit_id.text.toString()
                pw = edit_pw.text.toString()
                email = id + "@sangmyung.kr"
                val call = RetrofitObject.getRetrofitService.sendnum(Retrofit.Requestsendnum(email))
                call.enqueue(object : Callback<Retrofit.Responsesendnum> {
                    override fun onResponse(
                        call: Call<Retrofit.Responsesendnum>,
                        response: Response<Retrofit.Responsesendnum>
                    ) {
                        if (response.isSuccessful) {
                            val response = response.body()
                            if (response != null) {
                                if (response.success) {
                                    btn_sendnum.text = "인증번호 재전송"
                                    Toast.makeText(this@ActivitySingup, "인증 번호가 발송되었습니다.", Toast.LENGTH_SHORT).show()
                                    btn_sendnum.isEnabled = true
                                    sendnumcheck = true
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<Retrofit.Responsesendnum>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit", errorMessage)
                    }
                })
            }else{
                Toast.makeText(this@ActivitySingup, "학번을 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
                btn_sendnum.isEnabled = true
            }
        }
        //인증 번호 확인 버튼
        btn_checknum.setOnClickListener {
            btn_checknum.isEnabled = false
            val num = edit_checknum.text.toString()
            val call = RetrofitObject.getRetrofitService.checknum(Retrofit.Requestchecknum(email,num))
            call.enqueue(object : Callback<Retrofit.Responsechecknum> {
                override fun onResponse(
                    call: Call<Retrofit.Responsechecknum>,
                    response: Response<Retrofit.Responsechecknum>
                ) {
                    Log.d("Retrofit", response.code().toString())
                    if (response.isSuccessful) {
                        val response = response.body()
                        if (response != null) {
                            if (response.success) {
                                btn_checknum.visibility = View.GONE
                                btn_sendnum.visibility = View.GONE
                                edit_checknum.visibility = View.GONE
                                binding.signupTextSuccess.visibility = View.VISIBLE
                                binding.signupTextFail.visibility = View.INVISIBLE
                                emailcheck = true
                            }
                        }
                    }else{
                        binding.signupTextFail.visibility = View.VISIBLE
                        btn_checknum.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<Retrofit.Responsechecknum>, t: Throwable) {
                    btn_checknum.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }
        //프로필 생성으로 넘어가기 버튼
        btn_signup.setOnClickListener {
            if (emailcheck && pwcheck){
                val intent = Intent(this, ActivityProfile::class.java)
                intent.putExtra("id", email)
                intent.putExtra("pw", pw)
                startActivity(intent)
            }
        }
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