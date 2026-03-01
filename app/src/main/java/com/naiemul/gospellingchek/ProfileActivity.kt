package com.naiemul.gospellingchek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val buttonLog=findViewById<Button>(R.id.btnLogout)
        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile // প্রোফাইলে আছেন তাই এটি সিলেক্টেড থাকবে

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {R.id.nav_home -> {
                startActivity(Intent(this, CheckActivity::class.java))
                overridePendingTransition(0,0)
                finish()
                true
            }
                R.id.nav_scan -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }

        val pref = SharedPrefManager(this)

        // SharedPref থেকে ডাটা নিয়ে TextView-তে সেট করা
        // যদি নাম খালি থাকে তবে "Guest User" দেখাবে
        tvName.text = "Name: \n${pref.getUserName()}"
        tvEmail.text = "Email: \n${pref.getUserEmail()}"

        buttonLog.setOnClickListener {
            pref.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
}