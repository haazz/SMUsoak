package com.example.smu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smu.databinding.ActivityFindpwBinding

class ActivityFindpw : AppCompatActivity() {

    override fun onBackPressed() {
        val intent = Intent(this, ActivityLogin::class.java)
        startActivity(intent)
        finish()
    }

    private val binding: ActivityFindpwBinding by lazy { ActivityFindpwBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}