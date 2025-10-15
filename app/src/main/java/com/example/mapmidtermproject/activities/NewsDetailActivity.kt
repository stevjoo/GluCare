package com.example.mapmidtermproject.activities

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.models.NewsArticle

class NewsDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val ivArticleImage: ImageView = findViewById(R.id.ivArticleImage)
        val tvArticleTitle: TextView = findViewById(R.id.tvArticleTitle)
        val tvArticleContent: TextView = findViewById(R.id.tvArticleContent)

        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }

        val article = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_ARTICLE", NewsArticle::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_ARTICLE") as NewsArticle?
        }

        article?.let {
            ivArticleImage.setImageResource(it.imageResId)
            tvArticleTitle.text = it.title
            tvArticleContent.text = it.content
        }
    }
}