package com.example.e_challan

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PayFastRepository @Inject constructor() {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "Android E-Challan App")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(PayFastConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val payFastService = retrofit.create(PayFastService::class.java)

    suspend fun getAccessToken(
        basketId: String,
        amount: Double
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = payFastService.getAccessToken(
                merchantId = PayFastConfig.MERCHANT_ID,
                securedKey = PayFastConfig.SECURED_KEY,
                basketId = basketId,
                transactionAmount = amount.toString(),
                currencyCode = "PKR"
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.accessToken)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to get access token: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PayFastRepository", "Error getting access token", e)
            Result.failure(e)
        }
    }

    fun generatePaymentFormData(
        accessToken: String,
        paymentRequest: PaymentRequest
    ): Map<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        return mapOf(
            "MERCHANT_ID" to PayFastConfig.MERCHANT_ID,
            "MERCHANT_NAME" to PayFastConfig.MERCHANT_NAME,
            "TOKEN" to accessToken,
            "PROCCODE" to "00",
            "TXNAMT" to paymentRequest.amount.toString(),
            "CUSTOMER_MOBILE_NO" to paymentRequest.customerMobile,
            "CUSTOMER_EMAIL_ADDRESS" to paymentRequest.customerEmail,
            "SIGNATURE" to generateSignature(),
            "VERSION" to "ANDROID-APP-1.0",
            "TXNDESC" to paymentRequest.description,
            "SUCCESS_URL" to PayFastConfig.SUCCESS_URL,
            "FAILURE_URL" to PayFastConfig.FAILURE_URL,
            "BASKET_ID" to paymentRequest.basketId,
            "ORDER_DATE" to currentDate,
            "CHECKOUT_URL" to PayFastConfig.CHECKOUT_URL,
            "CURRENCY_CODE" to "PKR",
            "CUSTOMER_NAME" to paymentRequest.customerName,
            "TRAN_TYPE" to "ECOMM_PURCHASE"
        )
    }

    fun validatePaymentResponse(
        basketId: String,
        errorCode: String,
        validationHash: String
    ): Boolean {
        val stringToHash = "$basketId|${PayFastConfig.SECURED_KEY}|${PayFastConfig.MERCHANT_ID}|$errorCode"
        val calculatedHash = sha256(stringToHash)
        return calculatedHash == validationHash
    }

    private fun generateSignature(): String {
        return UUID.randomUUID().toString()
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}