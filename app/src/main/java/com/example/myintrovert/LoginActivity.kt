package com.example.myintrovert

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.myintrovert.databinding.ActivityLoginBinding
import com.example.myintrovert.service.ApiService
import com.example.myintrovert.service.LoginResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding?.btnLogin?.setOnClickListener {
            login()
        }

        binding?.tvSignup?.setOnClickListener {
            openSignupActivity()
        }
    }

    private fun openSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }


    private val apiService: ApiService by lazy {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.43.110/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(ApiService::class.java)
    }

    private fun login() {
        val email = binding?.emailLogin?.text.toString()
        val password = binding?.passwordLogin?.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Use CoroutineScope and launch a coroutine
        // Use CoroutineScope and launch a coroutine
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: Response<LoginResponse> = apiService.login(email, password)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login gagal 1", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        response.message()
                    } else {
                        errorBody
                    }
                    Toast.makeText(this@LoginActivity, "Login gagal 2: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login gagal 3: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}
