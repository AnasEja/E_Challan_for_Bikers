
package com.example.e_challan
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Apply animation to the logo ImageView
        val logo = findViewById<ImageView>(R.id.imageView)
        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        logo.startAnimation(animation)


        // Transition to MainPage after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }, 2000)
    }
}
