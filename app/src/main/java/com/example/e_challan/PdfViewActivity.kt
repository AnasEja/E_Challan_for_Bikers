package com.example.e_challan

import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import java.io.File
import java.io.FileOutputStream
import com.google.firebase.database.*
import com.example.e_challan.model.Challan



class PdfViewActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView
    private lateinit var dbRef: DatabaseReference
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        pdfView = findViewById(R.id.pdfView)
        progressBar = findViewById(R.id.progressBar)
        dbRef = FirebaseDatabase.getInstance().getReference("challans")

        val challanNo = intent.getStringExtra("challan_no")
        if (challanNo.isNullOrBlank()) {
            Toast.makeText(this, "No challan key provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🔹 Show loader while fetching PDF
        progressBar.visibility = View.VISIBLE

        dbRef.child(challanNo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = View.GONE   // Hide loader once data is ready

                val challan = snapshot.getValue(Challan::class.java)
                if (challan == null || challan.pdf_data.isNullOrBlank()) {
                    Toast.makeText(this@PdfViewActivity, "Challan PDF not available", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }

                try {
                    val pdfBytes: ByteArray = Base64.decode(challan.pdf_data, Base64.DEFAULT)

                    val safeNo = challan.challan_no?.replace("[^A-Za-z0-9_-]".toRegex(), "_") ?: "challan"
                    val outFile = File(cacheDir, "$safeNo.pdf")

                    FileOutputStream(outFile).use { fos ->
                        fos.write(pdfBytes)
                    }

                    pdfView.fromFile(outFile)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .load()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@PdfViewActivity, "Failed to render PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@PdfViewActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }
}
