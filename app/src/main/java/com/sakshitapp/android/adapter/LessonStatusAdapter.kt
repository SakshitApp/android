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
import com.sakshitapp.android.databinding.ListItemAddLessonBinding
import com.sakshitapp.android.databinding.ListItemStatusBinding
import com.sakshitapp.shared.model.Lesson

class LessonStatusAdapter(private val listener: Callback) :
    ListAdapter<Lesson, LessonStatusAdapter.ViewHolder>(LessonStatusDiffUtil()) {

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) == itemCount) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) NewLessonViewHolder(
            ListItemAddLessonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        ) else LessonViewHolder(
            ListItemStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(if ((position + 1) == itemCount) null else getItem(position))
    }

    open class ViewHolder(
        private val root: View
    ) : RecyclerView.ViewHolder(root) {
        open fun bind(item: Lesson?) {}
    }

    class LessonViewHolder(
        private val binding: ListItemStatusBinding,
        private val listener: Callback
    ) : ViewHolder(binding.root) {
        override fun bind(item: Lesson?) {
            binding.apply {
                Glide.with(root.context)
                    .load(item?.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_image_24)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
                    .into(image)
                title.text = item?.title ?: "<title>"
                summery.text = item?.description ?: "<summery>"
                stats.text = root.context.getString(
                    R.string.lesson_status,
                    item?.question?.size,
                    item?.likes,
                    item?.review?.size
                )
                delete.setOnClickListener {
                    listener.onDelete(item!!)
                }
                root.setOnClickListener {
                    listener.onClick(item!!)
                }
            }
        }
    }

    class NewLessonViewHolder(
        private val binding: ListItemAddLessonBinding,
        private val listener: Callback
    ) : ViewHolder(binding.root) {
        override fun bind(item: Lesson?) {
            binding.root.setOnClickListener {
                listener.onNew()
            }
        }
    }

    internal class LessonStatusDiffUtil : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onNew()
        fun onClick(lesson: Lesson)
        fun onDelete(lesson: Lesson)
    }
}