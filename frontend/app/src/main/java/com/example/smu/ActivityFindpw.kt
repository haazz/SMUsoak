package com.example.smu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smu.databinding.ActivityFindpwBinding

class ActivityFindpw : AppCompatActivity() {

    private val binding: ActivityFindpwBinding by lazy { ActivityFindpwBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}