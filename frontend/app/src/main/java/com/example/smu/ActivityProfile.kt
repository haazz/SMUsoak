package com.example.smu

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.smu.databinding.ActivityProfileBinding
import de.hdodenhof.circleimageview.CircleImageView

class ActivityProfile : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private lateinit var changeprofileimage: ImageButton
    private lateinit var makeprofilebtb: Button
    private lateinit var profile: CircleImageView
    private lateinit var mbti: Spinner
    private lateinit var gender: Spinner

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profile = binding.profileImageProfile
        if (uri != null) {
            Glide.with(this)
                .load(uri)
                .into(profile)
        }

        profile.setOnClickListener {
            if(uri != null){
                val intent = Intent(this, ActivityImage::class.java)
                intent.putExtra("image_uri", uri.toString())
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        changeprofileimage = binding.profileBtnChange

        changeprofileimage.setOnClickListener{
            getContent.launch("image/*")
        }

        mbti = binding.profileSpinnerMbti
        val mbtiArray = resources.getStringArray(R.array.mbti)
        setSpinner(mbti, mbtiArray)

        val dpValue = 130
        val pixels = (dpValue * Resources.getSystem().displayMetrics.density).toInt()
        gender = binding.profileSpinnerGender
        gender.setDropDownWidth(pixels)
        gender = binding.profileSpinnerGender	// spinner
        val genderArray = resources.getStringArray(R.array.gender)	// 배열
        setSpinner(gender, genderArray)

        makeprofilebtb = binding.profileBtnCreate
        makeprofilebtb.setOnClickListener {
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setSpinner(spinner: Spinner, array: Array<String>) {
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            array.toMutableList()
        ) {
            override fun getCount(): Int = super.getCount() - 1  // 힌트를 제외한 항목 수
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(adapter.count)  // 힌트를 선택한 상태로 설정
    }
}