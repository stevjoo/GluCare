package com.example.mapmidtermproject.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.adapters.CarouselAdapter
import com.example.mapmidtermproject.adapters.NewsAdapter
import com.example.mapmidtermproject.data.NewsData
import com.example.mapmidtermproject.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var carouselViewPager: ViewPager2
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Auth Init
        auth = Firebase.auth

        // Cek Login di awal
        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_main)

        // Setup View All
        val tvViewAll: TextView = findViewById(R.id.tvViewAll)
        tvViewAll.setOnClickListener {
            startActivity(Intent(this, AllNewsActivity::class.java))
        }

        // Carousel setup
        carouselViewPager = findViewById(R.id.carouselViewPager)
        val carouselImages = listOf(
            R.drawable.food_label_reading,
            R.drawable.diabetes_symptoms,
            R.drawable.glucarename
        )
        carouselViewPager.adapter = CarouselAdapter(carouselImages)

        // RecyclerView setup
        val rvNews = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this)
        val newsList = NewsData.getNewsList()
        val adapter = NewsAdapter(newsList) { article ->
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
        }
        rvNews.adapter = adapter

        setupBottomNavigation()
    }

    // --- FITUR KEAMANAN (AUTH GUARD) ---
    override fun onStart() {
        super.onStart()
        // Cek setiap kali user masuk ke halaman ini
        // Jika user null (misal baru saja dihapus), langsung tendang
        if (auth.currentUser == null) {
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_log -> {
                    startActivity(Intent(this, LogActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_camera -> {
                    startActivity(Intent(this, AnalysisActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT })
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}