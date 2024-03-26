package com.example.smu

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.smu.databinding.ActivityProfileBinding
import de.hdodenhof.circleimageview.CircleImageView

class ActivityProfile : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private lateinit var changeprofileimage: ImageButton
    private lateinit var profile: CircleImageView
    private lateinit var image_uri: Uri

    private val user = MySharedPreference.user
    private val token = user.getString("jwt", "")

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            image_uri = uri
            Glide.with(this)
                .load(uri)
                .into(profile)

            profile.setOnClickListener {
                val intent = Intent(this, ActivityImage::class.java)
                intent.putExtra("image_uri", image_uri.toString())
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.d("token", token.toString())

        changeprofileimage = binding.profileBtnChange
        profile = binding.profileImageProfile

        changeprofileimage.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}
