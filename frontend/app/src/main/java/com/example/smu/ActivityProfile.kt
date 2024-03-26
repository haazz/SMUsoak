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
    private lateinit var profile: CircleImageView

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
    }
}