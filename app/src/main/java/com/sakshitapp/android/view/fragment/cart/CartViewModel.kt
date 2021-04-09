package com.sakshitapp.android.view.fragment.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.shared.model.Cart
import com.sakshitapp.shared.model.RazorPayOrder
import com.sakshitapp.shared.model.RazorPayVerify
import com.sakshitapp.shared.repository.CartRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    private val _data: MutableLiveData<List<Cart>> by lazy {
        MutableLiveData()
    }

    private val _order: MutableLiveData<RazorPayOrder?> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<List<Cart>> = _data
    fun getOrder(): LiveData<RazorPayOrder?> = _order
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getCart()
            }.onSuccess {
                _progress.postValue(false)
                _data.postValue(it)
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun buyNow() {
        _data.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    repository.startTransaction()
                }.onSuccess {
                    _progress.postValue(false)
                    _order.postValue(it)
                }.onFailure {
                    _progress.postValue(false)
                    _error.postValue(it.localizedMessage)
                }
            }
        }
    }

    fun delete(course: String) {
        _data.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    repository.removeFromCart(course)
                }.onSuccess {
                    _progress.postValue(false)
                    _data.postValue(it)
                }.onFailure {
                    _progress.postValue(false)
                    _error.postValue(it.localizedMessage)
                }
            }
        }
    }

    fun verify(paymentId: String?, sig: String?) {
        _order.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    repository.endTransaction(RazorPayVerify(paymentId?:"", it.id, sig ?:""))
                }.onSuccess {
                    _progress.postValue(false)
                    _data.postValue(it)
                }.onFailure {
                    _progress.postValue(false)
                    _error.postValue(it.localizedMessage)
                }
            }
        }
        _order.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}