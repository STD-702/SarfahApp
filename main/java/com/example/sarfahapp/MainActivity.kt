package com.example.sarfahapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        redirectToAppropriateActivity()
    }

    private fun redirectToAppropriateActivity() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        val intent = if (isLoggedIn) {
            Intent(this, HomeActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}