    // com/example/e_challan/model/Challan.kt
    package com.example.e_challan.model

    import java.io.Serializable

    data class Challan(
        val challan_no: String? = null,
        val plate: String? = null,
        val name: String? = null,
        val cnic: String? = null,
        val model: String? = null,
        val year: Int? = null,
        val email: String? = null,
        val timestamp: String? = null,
        val status: String? = null,
        val amount: Int? = null,
        val pdf_data: String? = null // ✅ Base64 string pushed by your Python script
    ) : Serializable
