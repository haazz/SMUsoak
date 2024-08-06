package com.example.smu

import android.content.Intent
import android.graphics.Color
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

    private lateinit var textPw : TextView
    private lateinit var textPwCheck : TextView
    private lateinit var textCheckNick : TextView
    private lateinit var editPw : EditText
    private lateinit var editPwCheck : EditText
    private lateinit var editCheckNum : EditText
    private lateinit var editId : EditText
    private lateinit var editNickname : EditText
    private lateinit var btnSendNum : Button
    private lateinit var btnCheckNum : Button
    private lateinit var btnCheckNick : Button
    private lateinit var btnNextPage : Button
    private lateinit var spinnerMBTI: Spinner
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerAge: Spinner

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
    private val pwCheckWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if (inputText.isEmpty()) {
                textPwCheck.visibility = View.INVISIBLE
            } else {
                textPwCheck.visibility = View.VISIBLE
                if (inputText == editPw.text.toString()) {
                    textPwCheck.visibility = View.INVISIBLE
                    pwcheck = true
                } else {
                    textPwCheck.text = "비밀번호가 일치하지 않습니다."
                    pwcheck = false
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //비밀번호 양식 확인
    private val pwWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if (inputText.isEmpty()) {
                textPw.visibility = View.INVISIBLE
            } else {
                textPw.visibility = View.VISIBLE

                if (inputText.length in 8..20) {
                    textPw.visibility = View.INVISIBLE
                } else {
                    textPw.text = "비밀번호 형식이 올바르지 않습니다."
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //학번 양식 확인
    private val idWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            idcheck = inputText.toIntOrNull() != null
            if(idcheck && s!!.length==9) {
                btnSendNum.isEnabled = true
                btnSendNum.alpha = 1f
            }else{
                btnSendNum.isEnabled = false
                btnSendNum.alpha = 0.5f
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //인증 번호 입력 확인
    private val numWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if (inputText.length==6 && sendnumcheck){
                btnCheckNum.isEnabled = true
                btnCheckNum.alpha = 1f
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    //닉네임 입력 확인
    private val nicknameWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textCheckNick.visibility=View.INVISIBLE
            val inputText = s.toString()
            if(inputText.length<2 || inputText.length>8) {
                btnCheckNick.isEnabled = false
                btnCheckNick.alpha=0.5f
            }
            else{
                btnCheckNick.isEnabled = true
                btnCheckNick.alpha=1f
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
        editId = binding.signupEditId
        editPw = binding.signupEditPw
        editPwCheck = binding.signupEditPwCheck
        editCheckNum = binding.signupEditEmailnum
        editNickname = binding.signupEditNickname
        textPw = binding.signupTextPw
        textPwCheck = binding.signupTextPwcheck
        textCheckNick = binding.signupTextChecknick
        btnSendNum = binding.signupBtnSendnum
        btnCheckNum = binding.signupBtnChecknum
        btnCheckNick = binding.signupBtnChecknick
        btnNextPage = binding.signupBtnNext
        spinnerMBTI = binding.signupSpinnerMbti
        spinnerAge = binding.signupSpinnerAge
        spinnerGender = binding.signupSpinnerGender

        //edittext에 감시자 설정
        editPw.addTextChangedListener(pwWatcherListener)
        editPwCheck.addTextChangedListener(pwCheckWatcherListener)
        editId.addTextChangedListener(idWatcherListener)
        editCheckNum.addTextChangedListener(numWatcherListener)
        editNickname.addTextChangedListener(nicknameWatcherListener)

        //인증번호 전송 버튼
        btnSendNum.setOnClickListener {
            Toast.makeText(this@ActivitySingup, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show()
            btnSendNum.isEnabled = false
            if (idcheck && editId.text.length==9) {
                id = editId.text.toString()
                email = id + "@sangmyung.kr"
                val call = RetrofitObject.getRetrofitService.sendNum(Retrofit.RequestSendNum(email))
                call.enqueue(object : Callback<Retrofit.ResponseSendNum> {
                    override fun onResponse(call: Call<Retrofit.ResponseSendNum>, response: Response<Retrofit.ResponseSendNum>) {
                        Log.d("profile", response.toString())
                        if (response.isSuccessful) {
                            val response = response.body()
                            if (response != null) {
                                if (response.success) {
                                    btnSendNum.text = "인증번호 재전송"
                                    Toast.makeText(this@ActivitySingup, "인증 번호가 발송되었습니다.", Toast.LENGTH_SHORT).show()
                                    sendnumcheck = true
                                }
                            }
                        }else{
                            btnSendNum.isEnabled = true
                        }
                    }

                    override fun onFailure(call: Call<Retrofit.ResponseSendNum>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        btnSendNum.isEnabled = true
                        Log.d("Retrofit", errorMessage)
                    }
                })
            }else{
                Toast.makeText(this@ActivitySingup, "학번을 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
                btnSendNum.isEnabled = true
            }
        }

        //인증 번호 확인 버튼
        btnCheckNum.setOnClickListener {
            btnCheckNum.isEnabled = false
            val num = editCheckNum.text.toString()
            val call = RetrofitObject.getRetrofitService.checkNum(Retrofit.RequestCheckNum(email,num))
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        val response = response.body()
                        if (response != null) {
                            if (response.success) {
                                btnCheckNum.visibility = View.GONE
                                btnSendNum.visibility = View.GONE
                                editCheckNum.visibility = View.GONE
                                binding.signupTextSuccess.visibility = View.VISIBLE
                                binding.signupTextFail.visibility = View.INVISIBLE
                                emailcheck = true
                            }
                        }
                    }else{
                        binding.signupTextFail.visibility = View.VISIBLE
                        btnCheckNum.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    btnCheckNum.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnCheckNick.setOnClickListener {
            btnCheckNick.isEnabled = false
            val call = RetrofitObject.getRetrofitService.checkNick(editNickname.text.toString())
            call.enqueue(object : Callback<Retrofit.ResponseCheckNick> {
                override fun onResponse(call: Call<Retrofit.ResponseCheckNick>, response: Response<Retrofit.ResponseCheckNick>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            textCheckNick.visibility=View.VISIBLE
                            if (responseBody.data.available) {
                                nickcheck=true
                                textCheckNick.text="사용할 수 있는 닉네임입니다."
                                textCheckNick.setTextColor(Color.BLACK)
                            }else{
                                nickcheck=false
                                textCheckNick.text="이미 사용 중인 닉네임입니다."
                                textCheckNick.setTextColor(Color.RED)
                            }
                        }
                    }
                    btnCheckNick.isEnabled = true
                }

                override fun onFailure(call: Call<Retrofit.ResponseCheckNick>, t: Throwable) {
                    btnCheckNick.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        //mbti 스피너 설정
        val mbtiArray = resources.getStringArray(R.array.mbti)
        setSpinner(spinnerMBTI, mbtiArray)

        //age 스피너 설정
        val ageArray = resources.getStringArray(R.array.age)
        setSpinner(spinnerAge, ageArray)

        //gender 스피너 설정
        val genderArray = resources.getStringArray(R.array.gender)	// 배열
        setSpinner(spinnerGender, genderArray)

        //회원가입 후 프로필 설정으로 넘어감
        btnNextPage.setOnClickListener {
            val age = ageArray[spinnerAge.selectedItemPosition]
            val mbti = mbtiArray[spinnerMBTI.selectedItemPosition].toString()
            var gender = genderArray[spinnerGender.selectedItemPosition].toString()
            pw = editPw.text.toString()
            gender = if(gender=="남학우")
                "M"
            else
                "W"
            nickname = editNickname.text.toString()
            if(pwcheck && emailcheck && nickcheck && gender!="Gender" && age!="출생 연도"){
                var call = RetrofitObject.getRetrofitService.signUp(Retrofit.RequestSignUp(email,pw,age.toInt(),gender,mbti,nickname))
                if(mbti=="선택 안함"){
                    call = RetrofitObject.getRetrofitService.signUp(Retrofit.RequestSignUp(email,pw,age.toInt(),gender,null,nickname))
                }
                call.enqueue(object : Callback<Retrofit.ResponseToken> {
                    override fun onResponse(call: Call<Retrofit.ResponseToken>, response: Response<Retrofit.ResponseToken>) {
                        if (response.isSuccessful) {
                            val response = response.body()
                            if (response != null) {
                                if (response.success) {
                                    val token = response.data.accessToken
                                    val intent = Intent(this@ActivitySingup, ActivityProfile::class.java)
                                    intent.putExtra("token", token)
                                    intent.putExtra("mail", email)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }else{
                            Toast.makeText(this@ActivitySingup,response.message(),Toast.LENGTH_SHORT).show()
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