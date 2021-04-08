package com.sakshitapp.android.view.fragment.edit.question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.Lesson
import com.sakshitapp.shared.model.Question
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EditQuestionViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _data: MutableLiveData<Question> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<Question> = _data
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    private var course: Course? = null
    private var lessonId: String? = null
    private var questionId: String? = null


    fun loadDrafts(courseId: String?, lessonId: String?, questionId: String?) {
        this.lessonId = lessonId
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getDraftCourse(courseId)
            }.onSuccess {
                _progress.postValue(false)
                this@EditQuestionViewModel.course = it
                it.lessons.filter { it.uuid == lessonId }?.forEach {
                    if (questionId != null) {
                        _data.postValue(it.question.filter { it.uuid == questionId }.firstOrNull())
                    } else {
                        course?.let {
                            val lessons = arrayListOf<Lesson>()
                            it.lessons.forEach { lesson ->
                                if (lesson.uuid == lessonId) {
                                    val questions = arrayListOf<Question>()
                                    questions.addAll(lesson.question)
                                    questions.add(Question())
                                    lessons.add(lesson.copy(question = questions))
                                } else {
                                    lessons.add(lesson)
                                }
                            }
                            save(it.copy(lessons = lessons))
                        }
                    }
                }
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun save(course: Course) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.updateDraft(course)
            }.onSuccess {
                this@EditQuestionViewModel.course = it
                _progress.postValue(false)
                it.lessons.filter { it.uuid == lessonId }?.forEach {
                    if (questionId != null) {
                        _data.postValue(it.question.filter { it.uuid == questionId }.firstOrNull())
                    } else {
                        val question = it.question?.lastOrNull()
                        questionId = question?.uuid
                        _data.postValue(question)
                    }
                }
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun save(
        question: String?,
        ans1: String?,
        ans2: String?,
        ans3: String?,
        ans4: String?,
        correct: Int
    ) {
        val ans = when (correct) {
            4 -> ans4
            3 -> ans3
            2 -> ans2
            else -> ans1
        }
        val list = listOfNotNull(ans1, ans2, ans3, ans4)
        course?.let {
            val lessons = arrayListOf<Lesson>()
            it.lessons.forEach { lesson ->
                if (lesson.uuid == lessonId) {
                    val questions = arrayListOf<Question>()
                    lesson.question.forEach { q ->
                        if (q.uuid == questionId) {
                            questions.add(Question(questionId, question, list, ans ?: ""))
                        } else {
                            questions.add(q)
                        }
                    }
                    lessons.add(lesson.copy(question = questions))
                } else {
                    lessons.add(lesson)
                }
            }
            save(it.copy(lessons = lessons))
        }
    }
}