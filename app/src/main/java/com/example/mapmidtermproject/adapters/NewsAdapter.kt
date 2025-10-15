package com.example.mapmidtermproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.models.NewsArticle

// Tambahkan parameter click listener
class NewsAdapter(
    private val newsList: List<NewsArticle>,
    private val onItemClicked: (NewsArticle) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvNewsTitle)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvNewsDate)
        private val summaryTextView: TextView = itemView.findViewById(R.id.tvNewsSummary)
        private val imageView: ImageView = itemView.findViewById(R.id.ivNewsImage)

        fun bind(article: NewsArticle) {
            titleTextView.text = article.title
            dateTextView.text = article.date
            summaryTextView.text = article.summary
            imageView.setImageResource(article.imageResId)
            // Set listener di sini
            itemView.setOnClickListener {
                onItemClicked(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size
}