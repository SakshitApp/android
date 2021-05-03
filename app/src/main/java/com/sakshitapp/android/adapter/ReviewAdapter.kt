package com.sakshitapp.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ListItemReviewBinding
import com.sakshitapp.shared.model.Review

class ReviewAdapter: ListAdapter<Review, ReviewAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) {
            binding.apply {
                Glide.with(root.context)
                    .load(item.userImage)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_account_circle_24)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(48)))
                    .into(image)
                title.text = item.userName ?: "Unknown"
                subtitle.text = item.review
            }
        }
    }

    internal class MyDiffUtil : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

    }
}