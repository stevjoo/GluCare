package com.example.mapmidtermproject.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.adapters.NewsAdapter
import com.example.mapmidtermproject.models.NewsArticle

class AllNewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_news)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val rvAllNews: RecyclerView = findViewById(R.id.rvAllNews)
        rvAllNews.layoutManager = LinearLayoutManager(this)

        val dummyNews = createDummyNewsData()
        val adapter = NewsAdapter(dummyNews) { article ->
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
        }
        rvAllNews.adapter = adapter
    }

    // Anda bisa salin fungsi ini dari MainActivity atau buat data baru
    private fun createDummyNewsData(): List<NewsArticle> {
        val newsList = ArrayList<NewsArticle>()
        // ... (Isi dengan semua data berita Anda)
        newsList.add(NewsArticle(1, "5 Gejala Awal Diabetes yang Sering Diabaikan", "...", "2 Oktober 2025", R.drawable.sample_image, "Konten Lengkap..."))
        newsList.add(NewsArticle(2, "Pentingnya Pola Makan Sehat untuk Penderita Diabetes", "...", "1 Oktober 2025", R.drawable.sample_image, "Konten Lengkap..."))
        newsList.add(NewsArticle(3, "Olahraga Ringan yang Aman Dilakukan Setiap Hari", "...", "30 September 2025", R.drawable.sample_image, "Konten Lengkap..."))
        // Tambahkan lebih banyak berita di sini
        return newsList
    }
}