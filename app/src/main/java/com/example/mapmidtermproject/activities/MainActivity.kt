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
import com.example.mapmidtermproject.models.NewsArticle
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

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Setup "View All" click listener
        val tvViewAll: TextView = findViewById(R.id.tvViewAll)
        tvViewAll.setOnClickListener {
            startActivity(Intent(this, AllNewsActivity::class.java))
        }

        // Carousel setup
        carouselViewPager = findViewById(R.id.carouselViewPager)
        val carouselImages = listOf(
            R.drawable.sample_banner,
            R.drawable.glucaremobileapplogo,
            R.drawable.sample_image
        )
        carouselViewPager.adapter = CarouselAdapter(carouselImages)

        // RecyclerView setup with click listener
        val rvNews: androidx.recyclerview.widget.RecyclerView = findViewById(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this)

        val dummyNews = createDummyNewsData()
        val adapter = NewsAdapter(dummyNews) { article ->
            // Aksi yang dijalankan saat sebuah artikel di-klik
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
        }
        rvNews.adapter = adapter

        // Bottom Nav setup
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_camera -> {
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Updated function to match the new NewsArticle model
    private fun createDummyNewsData(): List<NewsArticle> {
        val newsList = ArrayList<NewsArticle>()
        val longContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation."

        newsList.add(
            NewsArticle(
                1,
                "5 Gejala Awal Diabetes yang Sering Diabaikan",
                "Banyak orang tidak menyadari tanda-tanda awal diabetes.",
                "2 Oktober 2025",
                R.drawable.sample_image,
                "Ini adalah konten lengkap untuk artikel 5 Gejala Awal Diabetes. " + longContent
            )
        )
        newsList.add(
            NewsArticle(
                2,
                "Pentingnya Pola Makan Sehat untuk Penderita Diabetes",
                "Mengatur asupan makanan adalah kunci utama dalam mengelola diabetes.",
                "1 Oktober 2025",
                R.drawable.sample_image,
                "Ini adalah konten lengkap untuk artikel Pola Makan Sehat. " + longContent
            )
        )
        newsList.add(
            NewsArticle(
                3,
                "Olahraga Ringan yang Aman Dilakukan Setiap Hari",
                "Aktivitas fisik tidak harus berat. Berikut jenis olahraga ringan.",
                "30 September 2025",
                R.drawable.sample_image,
                "Ini adalah konten lengkap untuk artikel Olahraga Ringan. " + longContent
            )
        )
        return newsList
    }
}