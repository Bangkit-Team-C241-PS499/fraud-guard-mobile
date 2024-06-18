package com.bangkit.fraudguard.data.adapters


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fraudguard.data.dto.response.ArticlesItem
import com.bangkit.fraudguard.databinding.ItemArticleSmallBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class SmallArticleListAdapter :
    ListAdapter<ArticlesItem, SmallArticleListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemArticleSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            binding.title.text = "${article.title}"
            Glide.with(binding.root)
                .load(article.urlToImage)
                .into(binding.image)
            binding.author.text = "oleh ${article.title}"
            binding.releaseDate.text = formatDate(article.publishedAt)
            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                it.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemArticleSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }

        private fun formatDate(dateTimeString: String?): String {
            if (dateTimeString == null) return ""

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateTimeString)
            return outputFormat.format(date!!)
        }

    }

}