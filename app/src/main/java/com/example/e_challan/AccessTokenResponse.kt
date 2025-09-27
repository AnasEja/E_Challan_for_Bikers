package com.example.e_challan

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
    @SerializedName("MERCHANT_ID")
    val merchantId: String,
    @SerializedName("ACCESS_TOKEN")
    val accessToken: String,
    @SerializedName("NAME")
    val name: String,
    @SerializedName("GENERATED_DATE_TIME")
    val generatedDateTime: String
)

data class PaymentRequest(
    val basketId: String,
    val amount: Double,
    val customerName: String,
    val customerEmail: String,
    val customerMobile: String,
    val description: String,
    val challanNumber: String? = null,
    val violationType: String? = null
)

data class PaymentResponse(
    @SerializedName("transaction_id")
    val transactionId: String?,
    @SerializedName("err_code")
    val errorCode: String,
    @SerializedName("err_msg")
    val errorMessage: String?,
    @SerializedName("basket_id")
    val basketId: String,
    @SerializedName("order_date")
    val orderDate: String,
    @SerializedName("validation_hash")
    val validationHash: String,
    @SerializedName("PaymentName")
    val paymentName: String?,
    @SerializedName("transaction_amount")
    val transactionAmount: String?,
    @SerializedName("merchant_amount")
    val merchantAmount: String?
)
