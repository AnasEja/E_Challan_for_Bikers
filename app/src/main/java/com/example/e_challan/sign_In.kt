package com.example.e_challan
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001
    private val ADMIN_EMAIL = "anasejaz23@gmail.com" // CHANGE THIS to your admin email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        val webClientId = getString(R.string.default_web_client_id).takeIf { it.isNotBlank() }
            ?: run {
                Toast.makeText(this, "Google Sign-In not configured", Toast.LENGTH_LONG).show()
                return
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val signInButton = findViewById<Button>(R.id.signIn)
        val signUpText = findViewById<TextView>(R.id.signup_main)
        val btnGoogleSignIn = findViewById<SignInButton>(R.id.googleSignInBtn)

        btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE)

        // Clickable "Sign Up"
        val fullText = "Don't have an account? Sign Up"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf("Sign Up")
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@SignIn, SignUp::class.java))
            }
        }, startIndex, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        signUpText.text = spannable
        signUpText.movementMethod = LinkMovementMethod.getInstance()
        signUpText.highlightColor = Color.TRANSPARENT

        // Email/password sign-in
        signInButton.setOnClickListener {
            val emailStr = email.text.toString().trim()
            val passwordStr = password.text.toString().trim()

            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        routeUser(auth.currentUser?.email)
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // Google Sign-In button
        btnGoogleSignIn.setOnClickListener {
            signOut {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    // Sign out from Firebase and Google before a fresh login
    private fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            onComplete()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    routeUser(auth.currentUser?.email)
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Decide where to go based on email
    private fun routeUser(email: String?) {
        if (email == ADMIN_EMAIL) {
            startActivity(Intent(this, Home::class.java)) // Admin page
        } else {
            startActivity(Intent(this, ClientHomeActivity::class.java)) // Client page
        }
        finish()
    }
}
