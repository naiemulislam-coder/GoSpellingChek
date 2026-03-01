package com.naiemul.gospellingchek

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CheckActivity : AppCompatActivity() {

    // এন্ডপয়েন্ট অবশ্যই /v2/check পর্যন্ত হতে হবে
    private val API_URL = "https://api.languagetool.org/v2/check"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check)

        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    AlertDialog.Builder(this@CheckActivity)
                        .setTitle("Exit")
                        .setMessage("তুমি কি অ্যাপ বন্ধ করতে চাও?")
                        .setPositiveButton("Yes") { _, _ ->
                            finishAffinity()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            })


        val etInput = findViewById<EditText>(R.id.etInputText)
        val tvOutput = findViewById<TextView>(R.id.etOutput)
        val buttonCheck = findViewById<Button>(R.id.btnCheck)
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)

// আপনি যদি এখন হোম পেজে থাকেন তবে এটি দিন:
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // যদি অলরেডি হোমে থাকেন তবে আবার ওপেন করার দরকার নেই
                    true
                }
                R.id.nav_scan -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }


        buttonCheck.setOnClickListener {
            val userInput = etInput.text.toString().trim()
            if (userInput.isNotEmpty()) {
                checkGrammar(userInput, tvOutput)
            } else {
                etInput.error = "Type here"
            }
        }
    }

    private fun checkGrammar(userInput: String, tvOutput: TextView) {
        tvOutput.text = "Checking... ⏳"

        // FormBody ঠিক আছে
        val requestBody = FormBody.Builder()
            .add("text", userInput)
            .add("language", "en-US")
            .build()

        val request = Request.Builder()
            .url(API_URL) // সংশোধিত URL
            .post(requestBody)
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { tvOutput.text = "Network Error: ${e.message}" }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val matches = jsonObject.getJSONArray("matches")

                        if (matches.length() == 0) {
                            runOnUiThread { tvOutput.text = "Grammar is perfect! ✅" }
                        } else {
                            // ভুলের তালিকা থেকে প্রথমটি এবং তার সাজেশন দেখানো
                            val firstMatch = matches.getJSONObject(0)
                            val message = firstMatch.getString("message")

                            // সাজেশন বের করা (যদি থাকে)
                            val replacements = firstMatch.getJSONArray("replacements")
                            val suggestion = if (replacements.length() > 0) {
                                replacements.getJSONObject(0).getString("value")
                            } else {
                                "No suggestions"
                            }

                            runOnUiThread {
                                tvOutput.text = "Issue:  $message\nSuggestion:  $suggestion"
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread { tvOutput.text = "Parse Error: ${e.message}" }
                    }
                } else {
                    runOnUiThread {
                        tvOutput.text = "API Error: ${response.code}\nআপনার টেক্সটটি ছোট করে চেষ্টা করুন।"
                    }
                }
            }
        })
    }
}

private fun OnBackPressedDispatcher.addCallback(
    owner: CheckActivity,
    onBackPressedCallback: Any
) {

 }