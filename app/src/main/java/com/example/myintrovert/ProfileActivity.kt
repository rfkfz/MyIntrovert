package com.example.myintrovert


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}
