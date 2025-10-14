package com.example.mapmidtermproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.adapters.CarouselAdapter
import com.example.mapmidtermproject.adapters.NewsAdapter
import com.example.mapmidtermproject.models.NewsArticle
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {

    private lateinit var carouselViewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        // --- Carousel setup ---
        carouselViewPager = findViewById(R.id.carouselViewPager)
        val carouselImages = listOf(
            R.drawable.sample_banner,
            R.drawable.glucaremobileapplogo,
            R.drawable.sample_image
        )
        carouselViewPager.adapter = CarouselAdapter(carouselImages)

        // --- RecyclerView setup ---
        val rvNews: androidx.recyclerview.widget.RecyclerView = findViewById(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this)

        val dummyNews = createDummyNewsData()
        val adapter = NewsAdapter(dummyNews)
        rvNews.adapter = adapter

        // --- Bottom Nav setup ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_camera -> true
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun createDummyNewsData(): List<NewsArticle> {
        val newsList = ArrayList<NewsArticle>()
        newsList.add(
            NewsArticle(
                1,
                "5 Gejala Awal Diabetes yang Sering Diabaikan",
                "Banyak orang tidak menyadari tanda-tanda awal diabetes. Kenali lima gejala umum ini sebelum terlambat.",
                "2 Oktober 2025"
            )
        )
        newsList.add(
            NewsArticle(
                2,
                "Pentingnya Pola Makan Sehat untuk Penderita Diabetes",
                "Mengatur asupan makanan adalah kunci utama dalam mengelola diabetes. Simak tips dari para ahli gizi.",
                "1 Oktober 2025"
            )
        )
        newsList.add(
            NewsArticle(
                3,
                "Olahraga Ringan yang Aman Dilakukan Setiap Hari",
                "Aktivitas fisik tidak harus berat. Berikut adalah beberapa jenis olahraga ringan yang sangat dianjurkan.",
                "30 September 2025"
            )
        )
        return newsList
    }
}
