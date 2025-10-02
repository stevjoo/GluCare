package com.example.mapmidtermproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardCheckLocation: MaterialCardView = findViewById(R.id.cardCheckLocation)
        val cardPhotoAnalysis: MaterialCardView = findViewById(R.id.cardPhotoAnalysis)
        val cardNews: MaterialCardView = findViewById(R.id.cardNews)

        // Aksi untuk menu Cek Lokasi
        cardCheckLocation.setOnClickListener {
            Toast.makeText(this, "Membuka peta lokasi...", Toast.LENGTH_SHORT).show()
        }

        // Aksi untuk menu Analisis Foto
        cardPhotoAnalysis.setOnClickListener {
            // Anda perlu membuat AnalysisActivity.kt
            Toast.makeText(this, "Membuka halaman analisis foto...", Toast.LENGTH_SHORT).show()
        }

        // Aksi untuk menu Berita (SUDAH DIPERBARUI)
        cardNews.setOnClickListener {
            // Pindah ke halaman Berita
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }
    }
}