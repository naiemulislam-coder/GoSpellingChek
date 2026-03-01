package com.naiemul.gospellingchek

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

// ১. ডাটা ক্লাস
data class CardInfo(var name: String = "", var dob: String = "")

class CameraActivity : AppCompatActivity() {

    private lateinit var ivBirthCert: ImageView
    private lateinit var ivNID: ImageView
    private lateinit var tvFinalResult: TextView

    private var birthData = CardInfo()
    private var nidData = CardInfo()

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        ivBirthCert = findViewById(R.id.ivBirthCert)
        ivNID = findViewById(R.id.ivNID)
        tvFinalResult = findViewById(R.id.tvFinalResult)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.nav_scan // ক্যামেরায় আছেন তাই এটি সিলেক্টেড থাকবে

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, CheckActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                R.id.nav_scan -> true // অলরেডি ক্যামেরায় আছেন
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                else -> false
            }
        }

        findViewById<Button>(R.id.btnCaptureBirth).setOnClickListener { openCamera(101) }
        findViewById<Button>(R.id.btnCaptureNID).setOnClickListener { openCamera(102) }

        findViewById<Button>(R.id.btnVerify).setOnClickListener {
            if (birthData.dob.isNotEmpty() && nidData.dob.isNotEmpty()) {
                compareAndShowResult()
            } else {
                Toast.makeText(this, "Please scan both cards first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera(code: Int) {
        // ক্যামেরা পারমিশন চেক করা হচ্ছে
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            // পারমিশন থাকলে ক্যামেরা ওপেন হবে
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, code)

        } else {
            // পারমিশন না থাকলে পারমিশন চাইবে
            androidx.core.app.ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), 100
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // ছবি পাওয়ার চেষ্টা করছি
            val bitmap = data?.extras?.get("data") as? Bitmap

            if (bitmap != null) {
                if (requestCode == 101) {
                    ivBirthCert.setImageBitmap(bitmap)
                    // ছবি সেট করার পর একবার চেক করার জন্য টোস্ট দিন
                    Toast.makeText(this, "Birth Cert Image Set", Toast.LENGTH_SHORT).show()
                    extractText(bitmap, true)
                } else if (requestCode == 102) {
                    ivNID.setImageBitmap(bitmap)
                    Toast.makeText(this, "NID Image Set", Toast.LENGTH_SHORT).show()
                    extractText(bitmap, false)
                }
            } else {
                // যদি বিটম্যাপ না পাওয়া যায়
                Toast.makeText(this, "Could not get image data!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractText(bitmap: Bitmap, isBirthCert: Boolean) {
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image).addOnSuccessListener { visionText ->
            val text = visionText.text

            // Regex দিয়ে জন্ম তারিখ বের করা (DD/MM/YYYY)
            val dob = Regex("\\b(\\d{2}[/-]\\d{2}[/-]\\d{4})\\b").find(text)?.value ?: ""

            if (isBirthCert) birthData.dob = dob else nidData.dob = dob

            Toast.makeText(this, "Scan Complete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compareAndShowResult() {
        val cleanBirthDob = birthData.dob.replace("[^0-9]".toRegex(), "")
        val cleanNidDob = nidData.dob.replace("[^0-9]".toRegex(), "")

        val result = if (cleanBirthDob == cleanNidDob && cleanBirthDob.isNotEmpty()) {
            "✅ VERIFIED\nBoth documents have the same Date of Birth: ${birthData.dob}"
        } else {
            "❌ MISMATCH FOUND\nBirth Cert: ${birthData.dob}\nNID Card: ${nidData.dob}"
        }
        tvFinalResult.text = result
    }
}