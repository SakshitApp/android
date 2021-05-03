package com.sakshitapp.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ListItemAddLessonBinding
import com.sakshitapp.android.databinding.ListItemStatusBinding
import com.sakshitapp.shared.model.Question

class QuestionStatusAdapter(private val listener: Callback) :
    ListAdapter<Question, QuestionStatusAdapter.ViewHolder>(QuestionStatusDiffUtil()) {

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) == itemCount) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) NewQuestionViewHolder(
            ListItemAddLessonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        ) else QuestionViewHolder(
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
        root: View
    ) : RecyclerView.ViewHolder(root) {
        open fun bind(item: Question?) {}
    }

    class QuestionViewHolder(
        private val binding: ListItemStatusBinding,
        private val listener: Callback
    ) : ViewHolder(binding.root) {
        override fun bind(item: Question?) {
            binding.apply {
                image.visibility = View.GONE
                title.text =
                    root.context.getString(R.string.question_status, item?.question ?: "<question>")
                summery.text =
                    root.context.getString(R.string.answer_status, item?.correct ?: "<answer>")
                stats.text =
                    root.context.getString(R.string.answer_option_status, item?.answers?.size ?: 0)
                delete.setOnClickListener {
                    listener.onDelete(item!!)
                }
                root.setOnClickListener {
                    listener.onClick(item!!)
                }
            }
        }
    }

    class NewQuestionViewHolder(
        private val binding: ListItemAddLessonBinding,
        private val listener: Callback
    ) : ViewHolder(binding.root) {
        override fun bind(item: Question?) {
            binding.root.setOnClickListener {
                listener.onNew()
            }
            binding.add.text = binding.root.context.getString(R.string.new_question)
        }
    }

    internal class QuestionStatusDiffUtil : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onNew()
        fun onClick(question: Question)
        fun onDelete(question: Question)
    }
}