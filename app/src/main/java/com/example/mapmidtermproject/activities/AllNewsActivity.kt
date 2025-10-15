package com.example.mapmidtermproject.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.adapters.NewsAdapter
import com.example.mapmidtermproject.data.NewsData // ← Tambahkan import ini
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

        // Ambil data dari file terpisah
        val newsList = NewsData.getNewsList()

        val adapter = NewsAdapter(newsList) { article ->
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
        }
        rvAllNews.adapter = adapter
    }
}