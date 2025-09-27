package com.example.e_challan

object PayFastConfig {
    // Test Credentials (Replace with production credentials when going live)
    const val MERCHANT_ID = "102"
    const val SECURED_KEY = "zWHjBp2AlttNu1sK"
    const val MERCHANT_NAME = "E-Challan App"

    // API Endpoints (UAT - Change to production URLs when going live)
    const val BASE_URL = "https://ipguat.apps.net.pk/"
    const val TOKEN_URL = "${BASE_URL}Ecommerce/api/Transaction/GetAccessToken"
    const val TRANSACTION_URL = "${BASE_URL}Ecommerce/api/Transaction/PostTransaction"

    // URLs for success/failure callbacks
    const val SUCCESS_URL = "https://yourapp.com/payment/success"
    const val FAILURE_URL = "https://yourapp.com/payment/failure"
    const val CHECKOUT_URL = "https://yourapp.com/payment/checkout"

    // Test Card Details (For testing only)
    object TestCredentials {
        const val CARD_NUMBER = "4166539182985688"
        const val CARD_EXPIRY = "09/26"
        const val CARD_CVV = "123"
        const val ACCOUNT_NUMBER = "11111111111111111111"
        const val CNIC = "111111111111"
        const val JAZZCASH_NUMBER = "03123456789"
    }
}