package com.sakshitapp.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ListItemStatusBinding
import com.sakshitapp.android.util.setOrGone
import com.sakshitapp.shared.model.Cart
import com.sakshitapp.shared.model.Course

class CartAdapter(private val listener: Callback) :
    ListAdapter<Cart, CartAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemStatusBinding,
        private val listener: Callback
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Cart?) {
            item?.let {
                binding.apply {
                    Glide.with(root.context)
                        .load(item.course?.image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .fallback(R.drawable.ic_baseline_image_24)
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
                        .into(image)
                    title.setOrGone(item.course?.title)
                    summery.setOrGone(item.course?.summery)
                    stats.visibility = View.GONE
                    delete.setOnClickListener {
                        item.course?.let { it1 -> listener.onDelete(it1) }
                    }
                    activate.visibility = View.GONE
                }
            }
        }
    }

    internal class MyDiffUtil : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onDelete(course: Course)
    }
}