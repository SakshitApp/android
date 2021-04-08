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
import com.sakshitapp.android.databinding.ListItemLessonBinding
import com.sakshitapp.shared.model.Lesson

class LessonAdapter(private val listener: Callback) :
    ListAdapter<Lesson, LessonAdapter.ViewHolder>(MyDiffUtil()) {

    var locked: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemLessonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            locked,
            listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemLessonBinding,
        private val locked: Boolean,
        private val listener: Callback
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Lesson) {
            binding.apply {
                Glide.with(root.context)
                    .load(item.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_image_24)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
                    .into(image)
                title.text = item.title ?: ""
                subtitle.text = item.description
                if (locked) {
                    icon.setImageResource(android.R.drawable.ic_lock_idle_lock)
                    icon.imageTintList = ContextCompat.getColorStateList(root.context, R.color.black_overlay)
                    root.setOnClickListener {
                        listener.onClick(item)
                    }
                } else {
                    icon.setImageResource(android.R.drawable.ic_media_play)
                    icon.imageTintList = ContextCompat.getColorStateList(root.context, R.color.black_overlay)
                    root.setOnClickListener {}
                }
            }
        }
    }

    internal class MyDiffUtil : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onClick(lesson: Lesson)
    }
}