package com.bangkit.fraudguard.data.adapters

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.databinding.ItemHistoryBinding
import com.bangkit.fraudguard.ui.history.HistoryDetailActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.round

class HistoryAdapter: ListAdapter<History, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(history: History) {
            binding.title.text = "${history.message}"
            binding.historyDate.text = "${formatDate(history.createdAt)} WIB"
            val roundedPrediction = history.prediction?.let { round(it).toInt() }
            binding.historyResultPrediction.text = "${roundedPrediction.toString()}%"
            if(history.label=="penipuan"){
                binding.historyTag.text = "Penipuan"
                binding.historyTag.backgroundTintList = ColorStateList.valueOf("#C44444".toColorInt())
                binding.historyResultPrediction.setTextColor("#C44444".toColorInt())
                binding.icon.setImageResource(R.drawable.spam)
            } else if(history.label=="normal"){
                binding.historyTag.text = "Aman"
                binding.historyTag.backgroundTintList = ColorStateList.valueOf("#4BC86E".toColorInt())
                binding.historyResultPrediction.setTextColor("#4BC86E".toColorInt())
                binding.icon.setImageResource(R.drawable.not_spam)
            } else if(history.label=="promo"){
                binding.historyTag.text = "Promosi"
                binding.historyTag.backgroundTintList = ColorStateList.valueOf("#C0CB43".toColorInt())
                binding.historyResultPrediction.setTextColor("#C0CB43".toColorInt())
                binding.icon.setImageResource(R.drawable.promo)
            } else {
                binding.predictionResult.visibility = android.view.View.INVISIBLE
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, HistoryDetailActivity::class.java)
            intent.putExtra("id", history?.id)
            holder.itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.context as Activity).toBundle())
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }
        }
        private fun formatDate(dateTimeString: String?): String {
            if (dateTimeString == null) return ""

            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
                outputFormat.timeZone = TimeZone.getDefault()
                val date = inputFormat.parse(dateTimeString)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                ""
            }
        }
    }

}