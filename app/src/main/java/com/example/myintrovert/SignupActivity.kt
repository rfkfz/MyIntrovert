package com.example.myintrovert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myintrovert.databinding.ActivitySignupBinding
import com.example.myintrovert.service.ApiService
import com.example.myintrovert.service.SignupResponse
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var apiService: ApiService
    private val selectImageRequestCode = 1
    private val readMediaPermissionRequestCode = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.43.110/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
        apiService = retrofit.create(ApiService::class.java)

        binding.btnSignup.setOnClickListener {
            signup()
        }

        binding.tvLogin.setOnClickListener {
            openLoginActivity()
        }

        binding.selectImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectImageFromGallery()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    readMediaPermissionRequestCode
                )
            }
        }
    }

    private fun uploadImageToServer(imageFile: File) {
        // Membuat request body dari file gambar
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        // Mengirim request ke server menggunakan Retrofit
        val call = apiService.uploadImage(imagePart)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Gambar berhasil diupload, tambahkan logika sesuai kebutuhan
                    Toast.makeText(this@SignupActivity, "Gambar berhasil diupload!", Toast.LENGTH_SHORT).show()
                } else {
                    // Terjadi kesalahan saat upload gambar
                    Toast.makeText(this@SignupActivity, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Gagal melakukan request ke server
                Toast.makeText(this@SignupActivity, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, selectImageRequestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == selectImageRequestCode && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val imagePath = RealPathUtil.getRealPath(this, it)
                if (imagePath != null) {
                    val imageFile = File(imagePath)
                    uploadImageToServer(imageFile)
                }
            }
        }
    }

    private fun signup() {
        val firstName = binding.firstNameSignup.text.toString()
        val lastName = binding.lastNameSignup.text.toString()
        val email = binding.emailSignup.text.toString()
        val password = binding.passwordSignup.text.toString()
        val confirmPassword = binding.confirmPasswordSignup.text.toString()
        val image = binding.selectImage.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Mohon isi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Konfirmasi Password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        val call = apiService.signup(firstName, lastName, email, password, image)
        call.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    val signupResponse = response.body()
                    // Handle response dari API sesuai kebutuhan
                    if (signupResponse?.success == true) {
                        Toast.makeText(this@SignupActivity, "Signup Berhasil!", Toast.LENGTH_SHORT).show()
                        openLoginActivity()
                    } else {
                        Toast.makeText(this@SignupActivity, "Gagal Code: 1", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Toast.makeText(this@SignupActivity, "Gagal Code: 2: $errorResponse", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Signup Gagal Code: 3", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}
