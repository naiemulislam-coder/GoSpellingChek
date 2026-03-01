package com.naiemul.gospellingchek

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val pref = SharedPrefManager(this)
        if (pref.isLoggedIn()) {
            // যদি আগে থেকেই লগইন করা থাকে, সরাসরি Home-এ যাবে
            startActivity(Intent(this, CheckActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)


    }
}