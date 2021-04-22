package com.sakshitapp.android.listener

import com.razorpay.PaymentResultWithDataListener

interface PaymentCallback: PaymentResultWithDataListener {
    var payListener: PaymentResultWithDataListener?
}