package com.sakshitapp.shared.repository

import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.Cart
import com.sakshitapp.shared.model.RazorPayOrder
import com.sakshitapp.shared.model.RazorPayVerify
import com.sakshitapp.shared.model.Response
import com.sakshitapp.shared.remote.CartAPI

class CartRepository {
    private val api = CartAPI()

    @Throws(Exception::class)
    suspend fun getCart(): List<Cart> {
        logMessage("CartRepository getCart")
        return api.getCart().data ?: emptyList()
    }

    @Throws(Exception::class)
    suspend fun addToCart(courseId: String): List<Cart> {
        logMessage("CartRepository addToCart $courseId")
        return api.addToCart(courseId).data ?: emptyList()
    }

    @Throws(Exception::class)
    suspend fun removeFromCart(courseId: String): List<Cart> {
        logMessage("CartRepository removeFromCart $courseId")
        return api.removeFromCart(courseId).data ?: emptyList()
    }

    @Throws(Exception::class)
    suspend fun startTransaction(): RazorPayOrder? {
        logMessage("CartRepository startTransaction")
        return api.startTransaction().data
    }

    @Throws(Exception::class)
    suspend fun endTransaction(data: RazorPayVerify): List<Cart> {
        logMessage("CartAPI endTransaction $data")
        return api.endTransaction(data).data ?: emptyList()
    }
}
