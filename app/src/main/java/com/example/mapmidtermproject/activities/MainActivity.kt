package com.example.mapmidtermproject.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private var sliderIndex = 0
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = object : Runnable {
        override fun run() {
            val count = carouselViewPager.adapter?.itemCount ?: 0
            if (count > 0) {
                sliderIndex = (carouselViewPager.currentItem + 1) % count
                carouselViewPager.setCurrentItem(sliderIndex, true)
            }
            sliderHandler.postDelayed(this, 5000)
        }
    }

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

        val tvViewAll: TextView = findViewById(R.id.tvViewAll)
        tvViewAll.setOnClickListener {
            startActivity(Intent(this, AllNewsActivity::class.java))
        }

        carouselViewPager = findViewById(R.id.carouselViewPager)
        val carouselImages = listOf(
            R.drawable.food_label_reading,
            R.drawable.diabetes_symptoms,
            R.drawable.glucarename
        )
        carouselViewPager.adapter = CarouselAdapter(carouselImages)
        carouselViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                sliderIndex = position
            }
        })

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

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 2000)
    }

    override fun onPause() {
        sliderHandler.removeCallbacks(sliderRunnable)
        super.onPause()
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
}
