package com.example.smu

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
    private lateinit var btn_next: Button
    private lateinit var btn_end: Button
    private lateinit var profile: CircleImageView
    private lateinit var imagePart: MultipartBody.Part
    private lateinit var mediaType: MediaType

    private val user = MySharedPreference.user
    private val token = "Bearer " + user.getString("token", "") //나중에 intent에서 가져오는걸로 수정이 필요함
    private val mail = user.getString("mail", "")

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = getRealPathFromUri(uri)
            val file = File(path)
            //jpg, jpeg, png인지 확인
            if(file.toString().substring(file.toString().length-3)=="jpg" || file.toString().substring(file.toString().length-4)=="jpeg"){
                mediaType = "image/jpeg".toMediaType()
                changeProfile(uri, file)
            }else if(file.toString().substring(file.toString().length-3)=="png"){
                mediaType = "image/png".toMediaType()
                changeProfile(uri, file)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        btn_next=binding.profileBtnNext
        btn_end=binding.profileBtnEnd

        changeprofileimage = binding.profileBtnChange
        profile = binding.profileImageProfile

        btn_next.setOnClickListener {
            val intent = Intent(this@ActivityProfile, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }

        btn_end.setOnClickListener {
            val data = JSONObject()
            data.put("mail", mail)
            val email = data.toString().toRequestBody("application/json".toMediaType())
            Log.d("profile", data.toString())
            val call = RetrofitObject.getRetrofitService.profile(token, imagePart, email)
            Log.d("profile", email.toString())
            call.enqueue(object : Callback<Retrofit.Responsesuccess> {
                override fun onResponse(call: Call<Retrofit.Responsesuccess>, response: Response<Retrofit.Responsesuccess>) {
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
                override fun onFailure(call: Call<Retrofit.Responsesuccess>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        changeprofileimage.setOnClickListener{
            when {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
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

    //권한 요청
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 설정하기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    private fun changeProfile(uri: Uri, file: File){
        Glide.with(this)
            .load(uri)
            .into(profile)
        val imageRequestBody = file.asRequestBody(mediaType)
        imagePart = MultipartBody.Part.createFormData("file", file.name, imageRequestBody)

        btn_end.isEnabled=true

        profile.setOnClickListener {
            val intent = Intent(this, ActivityImage::class.java)
            intent.putExtra("image_uri", uri.toString())
            startActivity(intent)
        }
    }
}
