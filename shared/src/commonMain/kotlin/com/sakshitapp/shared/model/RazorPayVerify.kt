package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class RazorPayVerify(
    val razorpay_payment_id: String,
    val razorpay_order_id: String,
    val razorpay_signature: String
)