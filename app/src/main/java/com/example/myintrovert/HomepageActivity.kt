package com.example.myintrovert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import android.view.View
import android.widget.Toast

class HomepageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val icProfile = findViewById<AppCompatImageView>(R.id.ic_profile)
        icProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

//        button.setOnClickListener {
//            val currentUserLabel = getCurrentUserLabelFromDatabase() // Mendapatkan label current user dari MySQL database
//
//            when (currentUserLabel) {
//                "introvert" -> {
//                    val intent = Intent(this, ArtikelActivity::class.java)
//                    startActivity(intent)
//                }
//                "ekstrovert" -> {
//                    val intent = Intent(this, ArtikelActivity2::class.java)
//                    startActivity(intent)
//                }
//                else -> {
//                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        // Fungsi untuk mendapatkan label current user dari MySQL database
//        private fun getCurrentUserLabelFromDatabase(): String {
//            // Kode untuk mengambil label current user dari MySQL database
//            // dan mengembalikan nilainya sebagai String
//        }

        val dashboardItem1 = findViewById<View>(R.id.DashboardItem1)
        dashboardItem1.setOnClickListener {
            startActivity(Intent(this, ArtikelActivity::class.java))
        }

        val dashboardItem2 = findViewById<View>(R.id.DashboardItem2)
        dashboardItem2.setOnClickListener {
            startActivity(Intent(this, VidioActivity::class.java))
        }

        val dashboardItem3 = findViewById<View>(R.id.DashboardItem3)
        dashboardItem3.setOnClickListener {
            startActivity(Intent(this, PodcastActivity::class.java))
        }

        val dashboardItem4 = findViewById<View>(R.id.DashboardItem4)
        dashboardItem4.setOnClickListener {
            startActivity(Intent(this, BukuActivity::class.java))
        }

    }

    fun openNotesActivity(view: View) {
        val intent = Intent(this, NotesActivity::class.java)
        startActivity(intent)
    }
    fun openJadwalActivity(view: View) {
        val intent = Intent(this, JadwalActivity::class.java)
        startActivity(intent)
    }
    fun openDiskusiActivity(view: View) {
        val intent = Intent(this, DiskusiActivity::class.java)
        startActivity(intent)
    }

}
