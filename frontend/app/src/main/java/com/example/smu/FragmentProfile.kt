package com.example.smu

import android.Manifest
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.smu.connection.Retrofit
import com.example.smu.connection.RetrofitObject
import com.example.smu.databinding.FragmentProfileBinding
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

class FragmentProfile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var imagePart: MultipartBody.Part
    private lateinit var mediaType: MediaType
    private lateinit var profile: ImageView
    private val user = MySharedPreference.user
    private val edit = user.edit()
    private val mail = user.getString("mail", "")
    private val token = user.getString("accessToken", "")

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = getRealPathFromUri(uri)
            val file = File(path!!)

            //jpg, jpeg, png인지 확인
            if(file.toString().endsWith("jpg") || file.toString().endsWith("jpeg")){
                mediaType = "image/jpeg".toMediaType()
            }else if(file.toString().endsWith("png")){
                mediaType = "image/png".toMediaType()
            }

            val imageRequestBody = file.asRequestBody(mediaType)
            imagePart = MultipartBody.Part.createFormData("file", file.name, imageRequestBody)

            val data = JSONObject()
            data.put("mail", mail)
            val email = data.toString().toRequestBody("application/json".toMediaType())
            val call = RetrofitObject.getRetrofitService.profile("Bearer $token", imagePart, email)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        changeProfile(uri)
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        profile = binding.fproImgProfile

        binding.fproBtnProfile.setOnClickListener {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    // Android 13 이상
                    if (ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        requestPermissions(arrayOf(READ_MEDIA_IMAGES), 1000)
                    }
                }
                else -> {
                    // Android 12 이하
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                    }
                }
            }
        }

        binding.fproBtnNick.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityChangeNick::class.java))
        }

        binding.fproBtnMbti.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityChangeMBTI::class.java))
        }

        binding.fproBtnSignout.setOnClickListener {
            edit.clear()
            startActivity(Intent(requireContext(), ActivityLogin::class.java))
            requireActivity().finish()
        }

        return binding.root
    }

    //이미지 저장 주소 가져오기
    private fun getRealPathFromUri(uri: Uri): String? {
        val context = requireContext()
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

    private fun changeProfile(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(profile)

        profile.setOnClickListener {
            val intent = Intent(requireContext(), ActivityImage::class.java)
            intent.putExtra("image_uri", uri.toString())
            startActivity(intent)
        }
    }
}