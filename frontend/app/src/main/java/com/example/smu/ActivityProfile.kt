package com.example.smu

import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.smu.databinding.ActivityProfileBinding
import de.hdodenhof.circleimageview.CircleImageView

class ActivityProfile : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private lateinit var changeprofileimage: ImageButton
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        changeprofileimage = binding.profileBtnChange

        changeprofileimage.setOnClickListener{
            getContent.launch("image/*")
        }

        mbti = binding.profileSpinnerMbti
        ArrayAdapter.createFromResource(this, R.array.mbti, android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mbti.adapter = adapter
        }

        val dpValue = 130
        val pixels = (dpValue * Resources.getSystem().displayMetrics.density).toInt()
        gender = binding.profileSpinnerGender
        gender.setDropDownWidth(pixels)
        ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gender.adapter = adapter
        }
    }
}