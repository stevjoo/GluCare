package com.example.mapmidtermproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.models.NewsArticle
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.adapters.NewsAdapter

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val rvNews: RecyclerView = findViewById(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this)

        val dummyNews = createDummyNewsData()
        val adapter = NewsAdapter(dummyNews)
        rvNews.adapter = adapter
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