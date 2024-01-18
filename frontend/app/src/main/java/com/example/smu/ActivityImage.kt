package com.example.smu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.smu.databinding.ActivityImageBinding

class ActivityImage : AppCompatActivity() {

    private val binding: ActivityImageBinding by lazy { ActivityImageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var image = binding.imageImage
        val imageUri = intent.getStringExtra("image_uri")

        if (imageUri != null) {
            Glide.with(this)
                .load(imageUri)
                .into(image)
        }

        binding.imageClose.setOnClickListener{
            finish()
        }
    }
}