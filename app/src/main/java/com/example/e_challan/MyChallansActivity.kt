package com.example.e_challan

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_challan.adapter.ChallanAdapter
import com.example.e_challan.model.Challan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyChallansActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChallanAdapter
    private val challanList = mutableListOf<Challan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_challans)

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerViewChallans)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChallanAdapter(challanList)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().getReference("challans")

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (userEmail == null) {
            Toast.makeText(this, "No logged in user!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Show loader before fetching
        progressBar.visibility = View.VISIBLE

        // Fetch challans for this user
        database.orderByChild("email").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    challanList.clear()
                    if (snapshot.exists()) {
                        for (challanSnap in snapshot.children) {
                            val challan = challanSnap.getValue(Challan::class.java)
                            challan?.let { challanList.add(it) }
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@MyChallansActivity, "No challan found", Toast.LENGTH_SHORT).show()
                    }
                    progressBar.visibility = View.GONE // 🔹 Hide loader after loading
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MyChallansActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
