package com.example.e_challan

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.e_challan.databinding.ActivityPayment2Binding
import com.example.e_challan.databinding.ActivityPaymentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityPayment2Binding
    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayment2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        observeViewModel()

        // Get payment details from intent
        val amount = intent.getDoubleExtra("AMOUNT", 0.0)
        val challanNumber = intent.getStringExtra("CHALLAN_NUMBER") ?: ""
        val customerName = intent.getStringExtra("CUSTOMER_NAME") ?: ""
        val customerEmail = intent.getStringExtra("CUSTOMER_EMAIL") ?: ""
        val customerMobile = intent.getStringExtra("CUSTOMER_MOBILE") ?: ""

        val paymentRequest = PaymentRequest(
            basketId = "CHALLAN-${System.currentTimeMillis()}",
            amount = amount,
            customerName = customerName,
            customerEmail = customerEmail,
            customerMobile = customerMobile,
            description = "E-Challan Payment for $challanNumber",
            challanNumber = challanNumber
        )

        viewModel.initiatePayment(paymentRequest)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url.toString()

                    // Check if it's a success or failure URL
                    when {
                        url.contains(PayFastConfig.SUCCESS_URL) -> {
                            handlePaymentSuccess(url)
                            return true
                        }
                        url.contains(PayFastConfig.FAILURE_URL) -> {
                            handlePaymentFailure(url)
                            return true
                        }
                    }
                    return false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.paymentState.observe(this) { state ->
            when (state) {
                is PaymentState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is PaymentState.ReadyForPayment -> {
                    loadPaymentForm(state.formData)
                }
                is PaymentState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
                else -> {}
            }
        }
    }

    private fun loadPaymentForm(formData: Map<String, String>) {
        val formHtml = buildFormHtml(formData)
        binding.webView.loadDataWithBaseURL(
            PayFastConfig.BASE_URL,
            formHtml,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun buildFormHtml(formData: Map<String, String>): String {
        val formBuilder = StringBuilder()
        formBuilder.append("""
            <html>
            <body onload="document.getElementById('payfast_form').submit();">
            <form id="payfast_form" action="${PayFastConfig.TRANSACTION_URL}" method="POST">
        """.trimIndent())

        formData.forEach { (key, value) ->
            formBuilder.append("""<input type="hidden" name="$key" value="$value"/>""")
        }

        formBuilder.append("""
            </form>
            <p>Redirecting to payment gateway...</p>
            </body>
            </html>
        """.trimIndent())

        return formBuilder.toString()
    }

    private fun handlePaymentSuccess(url: String) {
        val uri = Uri.parse(url)
        val transactionId = uri.getQueryParameter("transaction_id")
        val errorCode = uri.getQueryParameter("err_code")
        val basketId = uri.getQueryParameter("basket_id")
        val validationHash = uri.getQueryParameter("validation_hash")

        if (errorCode == "000" || errorCode == "00") {
            // Validate the response
            if (basketId != null && errorCode != null && validationHash != null) {
                val isValid = viewModel.validatePaymentResponse(basketId, errorCode, validationHash)

                if (isValid) {
                    Toast.makeText(this, "Payment Successful! Transaction ID: $transactionId", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Payment validation failed", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            handlePaymentFailure(url)
        }
    }

    private fun handlePaymentFailure(url: String) {
        val uri = Uri.parse(url)
        val errorMessage = uri.getQueryParameter("err_msg")
        Toast.makeText(this, "Payment Failed: $errorMessage", Toast.LENGTH_LONG).show()
        setResult(RESULT_CANCELED)
        finish()
    }
}