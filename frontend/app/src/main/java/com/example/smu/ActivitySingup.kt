package com.example.smu

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    private lateinit var edit_nickname : EditText
    private lateinit var btn_sendnum : Button
    private lateinit var btn_checknum : Button
    private lateinit var btn_nickname : Button
    private lateinit var btn_nextpage : Button
    private lateinit var spinner_mbti: Spinner
    private lateinit var spinner_gender: Spinner
    private lateinit var spinner_age: Spinner

    private lateinit var id : String
    private lateinit var pw : String
    private lateinit var email: String
    private lateinit var nickname : String

    //비밀번호 양식이 맞는치 체크
    private var pwcheck = false
    //학번 양식이 맞는지 체크
    private var idcheck = false
    //인증 번호가 전달 되었는지 체크
    private var sendnumcheck = false
    //이메일 인증이 되었는지 체크
    private var emailcheck = false
    //닉네임 중복 체크
    private var nickcheck= false

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

                if (inputText.length in 8..20) {
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
            if(idcheck && s!!.length==9) {
                btn_sendnum.isEnabled = true
                btn_sendnum.alpha = 1f
            }else{
                btn_sendnum.isEnabled = false
                btn_sendnum.alpha = 0.5f
            }
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

    //닉네임 입력 확인
    private val nicknamewatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if(inputText.length<2 || inputText.length>8) {
                btn_nickname.isEnabled = false
                btn_nickname.alpha=0.5f
            }
            else{
                btn_nickname.isEnabled = true
                btn_nickname.alpha=1f
            }
            nickcheck=false
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val binding: ActivitySingupBinding by lazy { ActivitySingupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.signupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //변수 초기화
        edit_id = binding.signupEditId
        edit_pw = binding.signupEditPw
        edit_pwcheck = binding.signupEditPwCheck
        edit_checknum = binding.signupEditEmailnum
        edit_nickname = binding.signupEditNickname
        text_pw = binding.signupTextPw
        text_pwcheck = binding.signupTextPwcheck
        btn_sendnum = binding.signupBtnSendnum
        btn_checknum = binding.signupBtnChecknum
        btn_nickname = binding.signupBtnChecknick
        btn_nextpage = binding.signupBtnNext
        spinner_mbti = binding.signupSpinnerMbti
        spinner_age = binding.signupSpinnerAge
        spinner_gender = binding.signupSpinnerGender

        //edittext에 감시자 설정
        edit_pw.addTextChangedListener(pwwatcherListener)
        edit_pwcheck.addTextChangedListener(pwcheckwatcherListener)
        edit_id.addTextChangedListener(idwatcherListener)
        edit_checknum.addTextChangedListener(numwatcherListener)
        edit_nickname.addTextChangedListener(nicknamewatcherListener)

        //인증번호 전송 버튼
        btn_sendnum.setOnClickListener {
            Toast.makeText(this@ActivitySingup, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show()
            btn_sendnum.isEnabled = false
            if (idcheck && edit_id.text.length==9) {
                id = edit_id.text.toString()
                email = id + "@sangmyung.kr"
                val call = RetrofitObject.getRetrofitService.sendNum(Retrofit.RequestSendNum(email))
                call.enqueue(object : Callback<Retrofit.ResponseSendNum> {
                    override fun onResponse(call: Call<Retrofit.ResponseSendNum>, response: Response<Retrofit.ResponseSendNum>) {
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

                    override fun onFailure(call: Call<Retrofit.ResponseSendNum>, t: Throwable) {
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
            val call = RetrofitObject.getRetrofitService.checkNum(Retrofit.RequestCheckNum(email,num))
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
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

                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    btn_checknum.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        //mbti 스피너 설정
        val mbtiArray = resources.getStringArray(R.array.mbti)
        setSpinner(spinner_mbti, mbtiArray)

        //age 스피너 설정
        val ageArray = resources.getStringArray(R.array.age)
        setSpinner(spinner_age, ageArray)

        //gender 스피너 설정
        val genderArray = resources.getStringArray(R.array.gender)	// 배열
        setSpinner(spinner_gender, genderArray)

        //회원가입 후 프로필 설정으로 넘어감
        btn_nextpage.setOnClickListener {
            val age = ageArray[spinner_age.selectedItemPosition]
            var mbti = mbtiArray[spinner_mbti.selectedItemPosition].toString()
            var gender = genderArray[spinner_gender.selectedItemPosition].toString()
            pw = edit_pw.text.toString()
            if(gender=="남학우")
                gender="M"
            else
                gender="W"
            nickname = edit_nickname.text.toString()
            if(pwcheck && emailcheck && gender!="Gender" && age!="출생 연도"){
                var call = RetrofitObject.getRetrofitService.signUp(Retrofit.RequestSignUp(email,pw,age.toInt(),gender,mbti,nickname))
                if(mbti=="선택 안함"){
                    call = RetrofitObject.getRetrofitService.signUp(Retrofit.RequestSignUp(email,pw,age.toInt(),gender,null,nickname))
                }
                Log.d("signup", age+gender+mbti+email+pw+nickname)
                call.enqueue(object : Callback<Retrofit.ResponseToken> {
                    override fun onResponse(call: Call<Retrofit.ResponseToken>, response: Response<Retrofit.ResponseToken>) {
                        if (response.isSuccessful) {
                            val response = response.body()
                            if (response != null) {
                                if (response.success) {
                                    val token = response.data.token
                                    val intent = Intent(this@ActivitySingup, ActivityProfile::class.java)
                                    intent.putExtra("token", token)
                                    intent.putExtra("mail", email)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }else{
                            Log.d("signup", response.toString())
                            Log.d("signup", response.body().toString())
                        }
                    }

                    override fun onFailure(call: Call<Retrofit.ResponseToken>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit", errorMessage)
                    }
                })
            }else{
                Toast.makeText(this@ActivitySingup, "회원가입 양식을 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //뒤로가기 버튼 눌렀을 때
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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

    private fun setSpinner(spinner: Spinner, array: Array<String>) {
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.spinner_text,
            array.toMutableList()
        ) {
            override fun getCount(): Int = super.getCount() - 1  // 힌트를 제외한 항목 수
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(adapter.count)  // 힌트를 선택한 상태로 설정
    }

    //뒤로가기 눌렀을 때
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@ActivitySingup, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }
    }
}