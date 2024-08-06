package com.example.smu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smu.databinding.ActivityChangeMbtiBinding

class ActivityChangeMBTI : AppCompatActivity() {

    private val binding: ActivityChangeMbtiBinding by lazy { ActivityChangeMbtiBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}