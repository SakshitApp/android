package com.sakshitapp.android.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ListItemQuizBinding
import com.sakshitapp.android.util.setOrGone
import com.sakshitapp.shared.model.Question
import kotlinx.coroutines.*

class QuizAdapter(private val listener: Callback, private val required: Int) :
    ListAdapter<Question, QuizAdapter.ViewHolder>(MyDiffUtil()) {

    var correct = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemQuizBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position) { isCorrect ->
            if (isCorrect) {
                correct++
            }
            GlobalScope.launch {
                delay(1000L)
                withContext(Dispatchers.Main) {
                    if ((position + 1) == itemCount) {
                        listener.onComplete(correct >= required)
                    } else {
                        listener.onSelect()
                    }
                }
            }
        }
    }

    class ViewHolder(
        private val binding: ListItemQuizBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Question, index: Int, onClick: (isCorrect: Boolean) -> Unit) {
            binding.apply {
                question.text =
                    root.context.getString(R.string.question_format, index + 1, item.question)
                setAnswer(ans1, item, 0, onClick)
                setAnswer(ans2, item, 1, onClick)
                setAnswer(ans3, item, 2, onClick)
                setAnswer(ans4, item, 3, onClick)
            }
        }

        private fun setAnswer(
            item: Button,
            question: Question,
            index: Int,
            onClick: (isCorrect: Boolean) -> Unit
        ) {
            item.isClickable = true
            item.setOrGone(question.answers.getOrNull(index))
            setBackground(item, R.color.purple_700, android.R.color.white)
            item.setOnClickListener {
                binding.apply {
                    ans1.isClickable = false
                    ans2.isClickable = false
                    ans3.isClickable = false
                    ans4.isClickable = false
                }
                if (question.answers.getOrNull(index) == question.correct) {
                    setBackground(item, android.R.color.holo_green_dark, android.R.color.white)
                    onClick(true)
                } else {
                    setBackground(item, android.R.color.holo_red_dark, android.R.color.white)
                    findCorrect(question)
                    onClick(false)
                }
            }
        }

        private fun findCorrect(item: Question) {
            binding.apply {
                when (item.correct) {
                    ans1.text -> setBackground(
                        ans1,
                        android.R.color.holo_green_dark,
                        android.R.color.white
                    )
                    ans2.text -> setBackground(
                        ans2,
                        android.R.color.holo_green_dark,
                        android.R.color.white
                    )
                    ans3.text -> setBackground(
                        ans3,
                        android.R.color.holo_green_dark,
                        android.R.color.white
                    )
                    ans4.text -> setBackground(
                        ans4,
                        android.R.color.holo_green_dark,
                        android.R.color.white
                    )
                }
            }
        }

        private fun setBackground(item: Button, backColor: Int, textColor: Int) {
            item.background = ColorDrawable(ContextCompat.getColor(item.context, backColor))
            item.setTextColor(ContextCompat.getColor(item.context, textColor))
        }
    }

    internal class MyDiffUtil : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }

    }

    interface Callback {
        fun onSelect()
        fun onComplete(isPassed: Boolean)
    }
}