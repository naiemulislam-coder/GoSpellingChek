package com.naiemul.gospellingchek

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import android.widget.TextView


import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SingUpActivity : AppCompatActivity() {
    lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sing_up)

        val tvPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val tvName = findViewById<TextInputEditText>(R.id.etName)
        val tvEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val tvUser = findViewById<TextInputEditText>(R.id.etUser)
        val tvNext = findViewById<TextView>(R.id.etNext)
        val buttonSing = findViewById<Button>(R.id.btnSing)

        tvNext.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        buttonSing.setOnClickListener {
            // .trim() ব্যবহার করা হয়েছে যাতে বাড়তি স্পেস না থাকে
            val eName = tvName.text.toString().trim()
            val eEmail = tvEmail.text.toString().trim()
            val ePassword = tvPassword.text.toString().trim()
            val eUser = tvUser.text.toString().trim()

            // সব ঘর পূরণ আছে কি না চেক
            if(eName.isEmpty() || eEmail.isEmpty() || ePassword.isEmpty() || eUser.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ফায়ারবেস ক্র্যাশ রোধ করতে স্পেশাল ক্যারেক্টার চেক
            if (eUser.contains(".") || eUser.contains("#") || eUser.contains("$") || eUser.contains("[") || eUser.contains("]")) {
                Toast.makeText(this, "Username cannot contain . # $ [ ]", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val user = MyData(eName, eEmail, ePassword, eUser)
            database = FirebaseDatabase.getInstance().getReference("Data")

            database.child(eUser).setValue(user).addOnSuccessListener {
                // ফিল্ডগুলো ক্লিয়ার করা
                tvName.text?.clear()
                tvEmail.text?.clear()
                tvPassword.text?.clear()
                tvUser.text?.clear()

                // SharedPref-এ অরিজিনাল ডাটা সেভ করা (ভুল ছিল এখানে)
                val pref = SharedPrefManager(this)
                pref.setLoginStatus(true)
                pref.saveUserProfile(eName, eEmail) // ইউজারের অরিজিনাল নাম ও ইমেইল সেভ হবে

                Toast.makeText(this, "Registration Successful ✅", Toast.LENGTH_SHORT).show()

                // রেজিস্ট্রেশন সফল হলে চেক অ্যাক্টিভিটিতে যাওয়া
                startActivity(Intent(this, CheckActivity::class.java))
                finish() // যাতে ব্যাক বাটন চাপলে আবার সাইন-আপ পেজে না আসে

            }.addOnFailureListener {
                Toast.makeText(this, "Registration Failed ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }
}