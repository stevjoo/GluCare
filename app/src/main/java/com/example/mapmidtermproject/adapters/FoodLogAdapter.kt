package com.example.mapmidtermproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmidtermproject.R
import com.example.mapmidtermproject.utils.FoodLog
import java.text.SimpleDateFormat
import java.util.Locale

class FoodLogAdapter(
    private val onDeleteClick: (FoodLog) -> Unit
) : RecyclerView.Adapter<FoodLogAdapter.ViewHolder>() {

    private var logs = listOf<FoodLog>()

    fun submitList(newLogs: List<FoodLog>) {
        // Urutkan dari yang terbaru (descending)
        logs = newLogs.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFood: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvSugar: TextView = itemView.findViewById(R.id.tvSugar)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

        fun bind(log: FoodLog) {
            tvFood.text = log.foodName
            tvSugar.text = "${log.bloodSugar} mg/dL"

            val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
            tvDate.text = sdf.format(log.timestamp)

            btnDelete.setOnClickListener {
                onDeleteClick(log)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount() = logs.size
}