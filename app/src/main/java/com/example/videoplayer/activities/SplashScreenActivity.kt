package com.example.videoplayer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.videoplayer.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AllowAccessActivity::class.java))
            finish()
        }, SPLASH_SCREEN_DURATION)
    }

    companion object {
        const val SPLASH_SCREEN_DURATION = 3000L
    }
}