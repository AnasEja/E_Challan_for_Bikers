package com.example.e_challan

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import generateParams

class payment_jazz : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()

        val params = generateParams()
        val html = """
            <html>
            <body onload="document.forms[0].submit()">
              <form method="post" action="https://sandbox.jazzcash.com.pk/CustomerPortal/TransactionManagement/Transaction/Index">
                ${params.entries.joinToString("") { "<input type='hidden' name='${it.key}' value='${it.value}' />" }}
              </form>
            </body>
            </html>
        """.trimIndent()

        webView.loadData(html, "text/html", "UTF-8")
    }
}