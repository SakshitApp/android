package com.sakshitapp.android.adapter

import android.view.LayoutInflater
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
import com.sakshitapp.android.databinding.ListItemCourseBinding
import com.sakshitapp.shared.model.Course

class CourseAdapter(private val listener: Callback) :
    ListAdapter<Course, CourseAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCourseBinding.inflate(
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
        private val binding: ListItemCourseBinding,
        private val listener: Callback
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Course) {
            binding.apply {
                Glide.with(root.context)
                    .load(item.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_image_24)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
                    .into(image)
                title.text = item.title ?: ""
                author.text = root.context.getString(
                    R.string.course_sub,
                    item.userName,
                    item.likes,
                    item.price
                )
                extra.text = item.summery
                root.setOnClickListener {
                    listener.onClick(item)
                }
            }
        }
    }

    internal class MyDiffUtil : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onClick(course: Course)
    }
}