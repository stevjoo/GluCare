package com.example.mapmidtermproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mapmidtermproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Pasang Splash Screen (HARUS sebelum super.onCreate)
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // 2. Inisialisasi & Cek status login SEKARANG
        auth = Firebase.auth
        if (auth.currentUser == null) {
            // Jika tidak ada user yang login, langsung arahkan ke LoginActivity
            // dan jangan lanjutkan eksekusi kode di MainActivity.
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Tutup MainActivity agar tidak bisa kembali ke sini
            return // Hentikan eksekusi fungsi onCreate lebih lanjut
        }

        // 3. Jika lolos pengecekan (user sudah login), baru tampilkan layout utama
        setContentView(R.layout.activity_main)

        // Setup listener untuk tombol-tombol di dashboard
        val cardCheckLocation: MaterialCardView = findViewById(R.id.cardCheckLocation)
        val cardPhotoAnalysis: MaterialCardView = findViewById(R.id.cardPhotoAnalysis)
        val cardNews: MaterialCardView = findViewById(R.id.cardNews)

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

    // Fungsi untuk menu logout (tetap sama)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            signOut()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}