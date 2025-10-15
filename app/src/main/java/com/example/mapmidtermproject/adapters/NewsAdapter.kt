package com.example.mapmidtermproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.models.NewsArticle

class NewsAdapter(
    private val newsList: List<NewsArticle>,
    private val onItemClick: (NewsArticle) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivNewsImage)
        val titleTextView: TextView = itemView.findViewById(R.id.tvNewsTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.tvNewsDate)
        val summaryTextView: TextView = itemView.findViewById(R.id.tvNewsSummary)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(newsList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        holder.imageView.setImageResource(article.imageResId)
        holder.titleTextView.text = article.title
        holder.dateTextView.text = article.date
        holder.summaryTextView.text = article.summary // ✅ INI HARUS "summary"
    }

    override fun getItemCount() = newsList.size
}