package com.example.e_challan

import retrofit2.Response
import retrofit2.http.*

interface PayFastService {

    @FormUrlEncoded
    @POST("Ecommerce/api/Transaction/GetAccessToken")
    suspend fun getAccessToken(
        @Field("MERCHANT_ID") merchantId: String,
        @Field("SECURED_KEY") securedKey: String,
        @Field("BASKET_ID") basketId: String,
        @Field("TXNAMT") transactionAmount: String,
        @Field("CURRENCY_CODE") currencyCode: String = "PKR"
    ): Response<AccessTokenResponse>
}