package com.example.mapmidtermproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mapmidtermproject.activities.AnalysisActivity
import com.example.mapmidtermproject.activities.LocationActivity
import com.example.mapmidtermproject.activities.NewsActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // API Splash Screen modern
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menghubungkan view dengan id-nya
        val cardCheckLocation: MaterialCardView = findViewById(R.id.cardCheckLocation)
        val cardPhotoAnalysis: MaterialCardView = findViewById(R.id.cardPhotoAnalysis)
        val cardNews: MaterialCardView = findViewById(R.id.cardNews)

        // DIUBAH: Aksi klik ini sekarang membuka LocationActivity
        cardCheckLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }

        cardPhotoAnalysis.setOnClickListener {
            startActivity(Intent(this, AnalysisActivity::class.java))
        }

        cardNews.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }
}