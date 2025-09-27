package com.example.e_challan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.view.CardInputWidget

class PaymentActivity : AppCompatActivity() {

    private lateinit var stripe: Stripe
    private lateinit var cardInputWidget: CardInputWidget
    private lateinit var payButton: Button
    private lateinit var btnEasypaisa: LinearLayout
    private lateinit var btnJazzCash: LinearLayout
    private lateinit var btnCardPayment: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Views
        cardInputWidget = findViewById(R.id.cardInputWidget)
        payButton = findViewById(R.id.payButton)
        btnEasypaisa = findViewById(R.id.btnEasypaisa)
        btnJazzCash = findViewById(R.id.btnJazzCash)
        btnCardPayment = findViewById(R.id.btnCardPayment)

        // Stripe init
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51Rydj6Q9FO9Z0XbJdx183XCOMiHG0Ae5IzeBjrzP0Ka3TwVtuzqZDLK3wCeJh85VdPb350Q2vXezeepbK6hS94WA00EmKaxeHN"
        )
        stripe = Stripe(
            applicationContext,
            PaymentConfiguration.getInstance(applicationContext).publishableKey
        )

        // Easypaisa demo
        btnEasypaisa.setOnClickListener {
            Toast.makeText(this, "✅ Easypaisa payment successful!", Toast.LENGTH_SHORT).show()
            // TODO: Save status in DB
        }

        // JazzCash demo
btnJazzCash.setOnClickListener(){
    startActivity(Intent(this, payment_jazz::class.java))
}
        // Show Card payment UI
        btnCardPayment.setOnClickListener {
            cardInputWidget.visibility = View.VISIBLE
            payButton.visibility = View.VISIBLE
        }

        // Handle Card payment
        payButton.setOnClickListener {
            val params = cardInputWidget.paymentMethodCreateParams
            if (params != null) {
                Toast.makeText(this, "✅ Card Payment Successful!", Toast.LENGTH_LONG).show()
                // TODO: Save to DB
            } else {
                Toast.makeText(this, "❌ Invalid Card Details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
