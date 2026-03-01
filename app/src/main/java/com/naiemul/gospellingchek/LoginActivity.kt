package com.naiemul.gospellingchek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {
    lateinit var dataBaseRe : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val buttonLogin=findViewById<Button>(R.id.btnLogin)
        val tvUser=findViewById<TextInputEditText>(R.id.etUser)
        val tvPassword=findViewById<TextInputEditText>(R.id.etPassword)
        val tvNext=findViewById<TextView>(R.id.etNext)

        tvNext.setOnClickListener {
            startActivity(Intent(this, SingUpActivity::class.java))
        }

        buttonLogin.setOnClickListener {

            val stUser=tvUser.text.toString()
            val stPassword =tvPassword.text.toString()

            if (stUser.isNotEmpty()&&stPassword.isNotEmpty()){
                readData(stUser,stPassword)
            }else{
                Toast.makeText(this,"Plies Enter your User Id", Toast.LENGTH_LONG).show()
            }

        }
//seOnclickEnd{}============================================
    }

    private fun readData(stUser: String, stPassword: String) {

        dataBaseRe = FirebaseDatabase.getInstance().getReference("Data")

        dataBaseRe.child(stUser).get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                val dbPassword = snapshot.child("epassword").value.toString()

                if (dbPassword == stPassword) {

                    // ১. ডাটাবেস থেকে ইউজারের নাম এবং ইমেইল নিয়ে আসা
                    // নিশ্চিত হোন আপনি SignUp এর সময় "ename" এবং "eemail" নামেই সেভ করেছিলেন কি না
                    val nameFromDB = snapshot.child("ename").value.toString()
                    val emailFromDB = snapshot.child("eemail").value.toString()

                    // ২. SharedPref-এ ডাটাবেস থেকে পাওয়া আসল তথ্য সেভ করা
                    val pref = SharedPrefManager(this)
                    pref.setLoginStatus(true)
                    pref.saveUserProfile(nameFromDB, emailFromDB) // এখানে আসল ডাটা সেভ হবে

                    Toast.makeText(this, "Login Successful ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CheckActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show()
        }
    }

}
