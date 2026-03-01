package com.naiemul.gospellingchek



import android.content.Context
import androidx.core.content.edit

class SharedPrefManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    // লগইন স্ট্যাটাস
    fun setLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit { putBoolean("isLoggedIn", isLoggedIn) }
    }
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean("isLoggedIn", false)

    // প্রোফাইল ডাটা সেভ করা (Signup/Login এর সময় এটি ব্যবহার করবেন)
    fun saveUserProfile(name: String, email: String) {
        sharedPreferences.edit {
            putString("user_name", name)
            putString("user_email", email)
        }
    }

    // প্রোফাইল ডাটা গেট করা
    fun getUserName(): String = sharedPreferences.getString("user_name", "No Name") ?: "No Name"
    fun getUserEmail(): String = sharedPreferences.getString("user_email", "No Email") ?: "No Email"

    fun logout() {
        sharedPreferences.edit { clear() }
    }
}

