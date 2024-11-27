package com.example.sarfahapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val phoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerLink = findViewById<Button>(R.id.btnRegister)

        loginButton.setOnClickListener {
            val phone = phoneNumber.text.toString().trim()
            val pass = password.text.toString().trim()

            if (phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                // Handle login
                val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}
