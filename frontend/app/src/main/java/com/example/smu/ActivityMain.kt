package com.example.smu

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smu.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityMain : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bottomNavigationView = binding.mainBottom

        supportFragmentManager
            .beginTransaction()
            .replace(binding.mainFrame.id, FragmentHome())
            .commitAllowingStateLoss()

        bottomNavigationView.selectedItemId = R.id.bnv_home

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bnv_home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.mainFrame.id, FragmentHome()) // Replace with your fragment
                        .commitAllowingStateLoss()
                    binding.mainToolbar.title = "SMU들다"
                    true
                }
                R.id.bnv_chat -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.mainFrame.id, FragmentChatMain())
                        .commitAllowingStateLoss()
                    binding.mainToolbar.title = "채팅"
                    true
                }
                R.id.bnv_board -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.mainFrame.id, FragmentBoard())
                        .commitAllowingStateLoss()
                    binding.mainToolbar.title = "게시판"
                    true
                }
                R.id.bnv_profile -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.mainFrame.id, FragmentProfile())
                        .commitAllowingStateLoss()
                    binding.mainToolbar.title = "프로필"
                    true
                }
                else -> false
            }
        }
    }
}