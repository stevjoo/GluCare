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
import com.example.mapmidtermproject.settings.SettingsActivity
import com.example.mapmidtermproject.utils.FirestoreHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var carouselViewPager: ViewPager2
    private lateinit var auth: FirebaseAuth
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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

        // RecyclerView News setup
        val rvNews = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this)

        // Adapter awal kosong
        newsAdapter = NewsAdapter(emptyList()) { article ->
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
        }
        rvNews.adapter = newsAdapter

        // --- LOAD BERITA ---
        // Ini akan otomatis upload dummy jika kosong, lalu menampilkannya
        loadNews()

        setupBottomNavigation()
    }

    private fun loadNews() {
        FirestoreHelper.getNews { newsList ->
            // Update UI saat data siap
            newsAdapter.updateData(newsList)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
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