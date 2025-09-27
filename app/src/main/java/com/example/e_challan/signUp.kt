package com.example.e_challan
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val mobile = findViewById<EditText>(R.id.editTextNumber)
        val name = findViewById<EditText>(R.id.editTextName)
        val cnic = findViewById<EditText>(R.id.editTextCNIC)
        val email = findViewById<EditText>(R.id.editTextEmail)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val confirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val agree = findViewById<CheckBox>(R.id.checkBoxAgree)
        val signUpBtn = findViewById<Button>(R.id.btnSignUp)

        signUpBtn.setOnClickListener {
            // Your existing validations here (checkbox, fields, passwords match)...

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User created successfully
                        val userId = auth.currentUser?.uid

                        // Prepare user data
                        val userInfo = hashMapOf(
                            "mobile" to mobile.text.toString(),
                            "name" to name.text.toString(),
                            "cnic" to cnic.text.toString(),
                            "email" to emailText
                        )

                        // Save to Firestore under collection "users"
                        if (userId != null) {
                            db.collection("users").document(userId)
                                .set(userInfo)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                                    // Navigate to Home or next screen
                                    val intent = Intent(this, SignIn::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}