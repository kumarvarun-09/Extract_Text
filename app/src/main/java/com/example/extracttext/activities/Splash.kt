package com.example.extracttext.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.extracttext.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this@Splash,
                    MainActivity::class.java
                )
            )
            finish()
        }, 3000)
    }
}