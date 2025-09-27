package com.example.e_challan
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2


class Home : AppCompatActivity() {

    private lateinit var carouselViewPager: ViewPager2
    private val imageList = listOf(
        R.drawable.carousol1,
        R.drawable.carousal2,
        R.drawable.carousol1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Carousel setup
        carouselViewPager = findViewById(R.id.carouselViewPager)
        carouselViewPager.adapter = CarouselAdapter(imageList)
        startAutoSlide()

        // Button listeners
        findViewById<LinearLayout>(R.id.btnViewChallan).setOnClickListener {
            startActivity(Intent(this, MyChallansActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnLivePreview).setOnClickListener {
            startActivity(Intent(this, LivePreviewActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, MyChallansActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnQuiz).setOnClickListener {
            startActivity(Intent(this, MyChallansActivity::class.java))
        }
    }

    private fun startAutoSlide() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = carouselViewPager.currentItem
                val nextItem = (currentItem + 1) % imageList.size
                carouselViewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnable, 3000)
    }
}
