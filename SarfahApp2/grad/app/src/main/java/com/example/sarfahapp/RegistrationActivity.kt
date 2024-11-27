package com.example.sarfahapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val firstName = findViewById<EditText>(R.id.etFirstName)
        val lastName = findViewById<EditText>(R.id.etLastName)
        val email = findViewById<EditText>(R.id.etEmail)
        val phoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        val password = findViewById<EditText>(R.id.etPassword)
        val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        registerButton.setOnClickListener {
            val fName = firstName.text.toString().trim()
            val lName = lastName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phone = phoneNumber.text.toString().trim()
            val pass = password.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (fName.isEmpty() || lName.isEmpty() || emailText.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            } else {
                // Handle registration
                Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
