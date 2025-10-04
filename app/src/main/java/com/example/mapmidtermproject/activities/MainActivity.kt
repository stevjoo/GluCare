package com.example.mapmidtermproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mapmidtermproject.activities.AnalysisActivity
import com.example.mapmidtermproject.activities.NewsActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // *** PENTING: pasang splash sebelum super.onCreate() ***
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Optional: tahan splash sampai kondisi terpenuhi
        // splashScreen.setKeepOnScreenCondition { /* return true while loading */ false }

        // Cari view dengan safe-call (menghindari NPE bila id berubah)
        val cardCheckLocation = findViewById<MaterialCardView?>(R.id.cardCheckLocation)
        val cardPhotoAnalysis = findViewById<MaterialCardView?>(R.id.cardPhotoAnalysis)
        val cardNews = findViewById<MaterialCardView?>(R.id.cardNews)

        // Jika salah satu view null, beri tahu agar mudah debug
        if (cardCheckLocation == null || cardPhotoAnalysis == null || cardNews == null) {
            Toast.makeText(this, "Cek layout activity_main.xml: ada view yang tidak ditemukan (id mismatch).", Toast.LENGTH_LONG).show()
        }

        cardCheckLocation?.setOnClickListener {
            Toast.makeText(this, "Membuka peta lokasi...", Toast.LENGTH_SHORT).show()
        }

        cardPhotoAnalysis?.setOnClickListener {
            startActivity(Intent(this, AnalysisActivity::class.java))
        }

        cardNews?.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }
}
