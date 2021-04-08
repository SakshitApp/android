package com.sakshitapp.android.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ListItemBigCourseBinding
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.Subscription

class BigCourseAdapter(private val showSmall: Boolean = true, private val listener: Callback) :
    ListAdapter<Subscription, BigCourseAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = ListItemBigCourseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        if (itemCount > 1 && showSmall) {
            layout.root.layoutParams = ViewGroup.LayoutParams(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    128f,
                    parent.context.resources.displayMetrics
                ).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return ViewHolder(
            layout,
            listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemBigCourseBinding,
        private val listener: Callback
    ) : RecyclerView.ViewHolder(binding.root) {

         fun bind(item: Subscription) {
                binding.apply {
                    Glide.with(root.context)
                        .load(item.course?.image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
                        .fallback(R.drawable.ic_baseline_image_24)
                        .into(image)
                    title.text = item.course?.title ?: ""
                    author.text = root.context.getString(
                        R.string.my_course_sub,
                        item.course?.userName,
                        item.course?.likes
                    )
                    extra.text = item.course?.summery
                    if (item.course?.lessons?.isNotEmpty() == true) {
                        progress.progress = (100 * item.progress.count())/(item.course?.lessons?.size ?: 1)
                    } else {
                        progress.visibility = View.GONE
                    }
                    root.setOnClickListener {
                        listener.onClick(item)
                    }
                }
        }
    }

    internal class MyDiffUtil : DiffUtil.ItemCallback<Subscription>() {
        override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onClick(course: Subscription)
    }
}