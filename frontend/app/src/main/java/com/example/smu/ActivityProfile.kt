package com.example.smu

import android.Manifest
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.ActivityProfileBinding
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ActivityProfile : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private lateinit var changeprofileimage: ImageButton
    private lateinit var btnNext: Button
    private lateinit var btnEnd: Button
    private lateinit var profile: CircleImageView
    private lateinit var imagePart: MultipartBody.Part
    private lateinit var mediaType: MediaType

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = getRealPathFromUri(uri)
            val file = File(path)
            //jpg, jpeg, png인지 확인
            if(file.toString().endsWith("jpg") || file.toString().endsWith("jpeg")){
                mediaType = "image/jpeg".toMediaType()
                changeProfile(uri, file)
            }else if(file.toString().endsWith("png")){
                mediaType = "image/png".toMediaType()
                changeProfile(uri, file)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val token = "Bearer " + intent.getStringExtra("token")
        val mail = intent.getStringExtra("mail")

        btnNext=binding.profileBtnNext
        btnEnd=binding.profileBtnEnd

        changeprofileimage = binding.profileBtnChange
        profile = binding.profileImageProfile

        btnNext.setOnClickListener {
            val intent = Intent(this@ActivityProfile, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }

        btnEnd.setOnClickListener {
            val data = JSONObject()
            data.put("mail", mail)
            val email = data.toString().toRequestBody("application/json".toMediaType())
            val call = RetrofitObject.getRetrofitService.profile(token, imagePart, email)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    Log.d("profile", response.toString())
                    if (response.isSuccessful) {
                        val response = response.body()
                        if (response != null) {
                            if (response.success) {
                                val intent = Intent(this@ActivityProfile, ActivityLogin::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        changeprofileimage.setOnClickListener{
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    // Android 13 이상
                    if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        requestPermissions(arrayOf(READ_MEDIA_IMAGES), 1000)
                    }
                }
                else -> {
                    // Android 12 이하
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                    }
                }
            }
        }
    }

    //이미지 저장 주소 가져오기
    private fun getRealPathFromUri(uri: Uri): String? {
        val context = applicationContext
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun changeProfile(uri: Uri, file: File){
        Glide.with(this)
            .load(uri)
            .into(profile)
        val imageRequestBody = file.asRequestBody(mediaType)
        imagePart = MultipartBody.Part.createFormData("file", file.name, imageRequestBody)

        btnEnd.isEnabled=true

        profile.setOnClickListener {
            val intent = Intent(this, ActivityImage::class.java)
            intent.putExtra("image_uri", uri.toString())
            startActivity(intent)
        }
    }
}
