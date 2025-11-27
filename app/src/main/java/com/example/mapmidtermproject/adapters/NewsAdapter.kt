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
    private var newsList: List<NewsArticle>,
    private val onItemClick: (NewsArticle) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    fun updateData(newList: List<NewsArticle>) {
        newsList = newList
        notifyDataSetChanged()
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivNewsImage)
        val titleTextView: TextView = itemView.findViewById(R.id.tvNewsTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.tvNewsDate)
        val summaryTextView: TextView = itemView.findViewById(R.id.tvNewsSummary)

        init {
            itemView.setOnClickListener {
                onItemClick(newsList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        holder.titleTextView.text = article.title
        holder.dateTextView.text = article.date
        holder.summaryTextView.text = article.summary

        // LOGIC GAMBAR DATABASE
        val context = holder.itemView.context
        // Cari gambar di folder drawable berdasarkan nama string dari database
        val imageResId = context.resources.getIdentifier(article.imageCode, "drawable", context.packageName)

        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId)
        } else {
            // Gambar default jika nama file tidak ditemukan/salah ketik
            holder.imageView.setImageResource(R.drawable.ic_image_placeholder)
        }
    }

    override fun getItemCount() = newsList.size
}