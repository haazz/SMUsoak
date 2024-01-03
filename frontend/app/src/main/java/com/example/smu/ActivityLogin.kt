package com.example.smu

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.smu.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val autologin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val btn_singin = binding.loginBtnSignin
        val btn_findpw = binding.loginBtnFindpw
        val btn_signup = binding.loginBtnSingup
        val checkauto = binding.loginCheck

        btn_signup.setOnClickListener{
            val intent = Intent(this, ActivitySingup::class.java)
            startActivity(intent)
            finish()
        }

        btn_singin.setOnClickListener {
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finish()
        }

        btn_findpw.setOnClickListener {
            val intent = Intent(this, ActivityFindpw::class.java)
            startActivity(intent)
            finish()
        }
    }
}