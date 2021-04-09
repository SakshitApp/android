package com.sakshitapp.shared.remote

import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.*
import io.ktor.client.request.*

class CartAPI: ServerAPI() {

    @Throws(Exception::class)
    suspend fun getCart(): Response<List<Cart>> {
        logMessage("CartAPI getCart")
        return httpClient.get("${URL}/cart")
    }

    @Throws(Exception::class)
    suspend fun addToCart(courseId: String): Response<List<Cart>> {
        logMessage("CartAPI addToCart $courseId")
        return httpClient.put("${URL}/cart/$courseId")
    }

    @Throws(Exception::class)
    suspend fun removeFromCart(courseId: String): Response<List<Cart>> {
        logMessage("CartAPI removeFromCart $courseId")
        return httpClient.delete("${URL}/cart/$courseId")
    }

    @Throws(Exception::class)
    suspend fun startTransaction(): Response<RazorPayOrder> {
        logMessage("CartAPI startTransaction")
        return httpClient.get("${URL}/cart/transaction")
    }

    @Throws(Exception::class)
    suspend fun endTransaction(data: RazorPayVerify): Response<List<Cart>> {
        logMessage("CartAPI endTransaction $data")
        return httpClient.post("${URL}/cart/transaction") {
            body = data
        }
    }
}