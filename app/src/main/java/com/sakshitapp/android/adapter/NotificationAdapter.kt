package com.sakshitapp.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sakshitapp.android.databinding.ListItemNotificationBinding
import com.sakshitapp.shared.model.Notification

class NotificationAdapter :
    ListAdapter<Notification, NotificationAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemNotificationBinding.inflate(
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
        private val binding: ListItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            binding.apply {
                title.text = item.title ?: ""
                description.text = item.content ?: ""
            }
        }
    }

    internal class MyDiffUtil : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}