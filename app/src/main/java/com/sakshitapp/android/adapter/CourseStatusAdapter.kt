package com.sakshitapp.android.adapter

import android.view.LayoutInflater
import android.view.View
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
import com.sakshitapp.android.databinding.ListItemEmptyBinding
import com.sakshitapp.android.databinding.ListItemStatusBinding
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.Course

class CourseStatusAdapter(private val listener: Callback) :
    ListAdapter<Course, CourseStatusAdapter.ViewHolder>(CourseStatusDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DraftViewHolder(
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

    open class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        open fun bind(item: Course?) {}
    }

    class EmptyViewHolder(
        binding: ListItemEmptyBinding
    ) : ViewHolder(binding.root)

    class DraftViewHolder(
        private val binding: ListItemStatusBinding,
        private val listener: Callback
    ) : ViewHolder(binding.root) {

        override fun bind(item: Course?) {
            item?.let {
                binding.apply {
                    Glide.with(root.context)
                        .load(item.image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .fallback(R.drawable.ic_baseline_image_24)
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
                        .into(image)
                    title.text = item.title ?: "<title>"
                    summery.text = item.summery ?: "<summery>"
                    stats.text = root.context.getString(
                        R.string.course_status,
                        item.state.name,
                        item.likes,
                        item.subscriber,
                        item.review.size
                    )
                    delete.setOnClickListener {
                        listener.onDelete(item)
                    }
                    root.setOnClickListener {
                        listener.onClick(item)
                    }
                    activate.visibility = View.VISIBLE
                    when (item.state) {
                        CourseState.DRAFT -> {
                            activate.imageTintList =
                                ContextCompat.getColorStateList(root.context, android.R.color.black)
                            activate.setOnClickListener {
                                listener.onStateChange(item, CourseState.ACTIVE)
                            }
                        }
                        CourseState.INACTIVE -> {
                            activate.imageTintList = ContextCompat.getColorStateList(
                                root.context,
                                android.R.color.holo_red_light
                            )
                            activate.setOnClickListener {
                                listener.onStateChange(item, CourseState.ACTIVE)
                            }
                        }
                        else -> {
                            activate.imageTintList = ContextCompat.getColorStateList(
                                root.context,
                                android.R.color.holo_green_light
                            )
                            activate.setOnClickListener {
                                listener.onStateChange(item, CourseState.INACTIVE)
                            }
                        }
                    }
                }
            }
        }
    }

    internal class CourseStatusDiffUtil : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onClick(course: Course)
        fun onDelete(course: Course)
        fun onStateChange(course: Course, state: CourseState)
    }
}