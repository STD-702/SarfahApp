package com.example.sarfahapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var middleName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initViews()
        setupFocusChangeListeners()
        registerButton.setOnClickListener { register() }
    }

    private fun initViews() {
        firstName = findViewById(R.id.first_name)
        middleName = findViewById(R.id.middle_name)
        lastName = findViewById(R.id.last_name)
        email = findViewById(R.id.email)
        phoneNumber = findViewById(R.id.phone_number)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        registerButton = findViewById(R.id.register_button)
    }

    private fun setupFocusChangeListeners() {
        firstName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateFirstName(firstName.text.toString())
        }

        middleName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateMiddleName(middleName.text.toString())
        }

        lastName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateLastName(lastName.text.toString())
        }

        email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateEmail(email.text.toString())
        }

        phoneNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePhone(phoneNumber.text.toString())
        }

        password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePassword(password.text.toString())
        }

        confirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateConfirmPassword(confirmPassword.text.toString(), password.text.toString())
        }
    }

    private fun register() {
        val fName = firstName.text.toString().trim()
        val mName = middleName.text.toString().trim()
        val lName = lastName.text.toString().trim()
        val emailText = email.text.toString().trim()
        val phone = phoneNumber.text.toString().trim()
        val pass = password.text.toString().trim()
        val confirmPass = confirmPassword.text.toString().trim()

        if (validateInputs(fName, mName, lName, emailText, phone, pass, confirmPass)) {
            sendRegistrationRequest(fName, mName, lName, emailText, phone, pass)
        }
    }

    private fun validateInputs(fName: String, mName: String, lName: String, emailText: String, phone: String, pass: String, confirmPass: String): Boolean {
        return when {
            fName.isEmpty() || mName.isEmpty() || lName.isEmpty() || emailText.isEmpty() ||
                    phone.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() -> {
                Toast.makeText(this, "All fields are required. Please fill in all fields.", Toast.LENGTH_LONG).show()
                false
            }
            !isValidEmail(emailText) -> {
                Toast.makeText(this, "Invalid email format. Please enter a valid email.", Toast.LENGTH_LONG).show()
                false
            }
            !phone.startsWith("05") -> {
                Toast.makeText(this,"Phone number must start with 05", Toast.LENGTH_LONG).show()
                false
            }
            pass != confirmPass -> {
                Toast.makeText(this,"Passwords do not match. Please try again.", Toast.LENGTH_LONG).show()
                false
            }
            !isValidPassword(pass) -> {
                Toast.makeText(this, "Password must be at least 8 characters long, include at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character, with no spaces.", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    private fun validateFirstName(firstName: String) {
        if (firstName.isEmpty()) {
            showToast("First name is required.")
        }
    }

    private fun validateMiddleName(middleName: String) {
        if (middleName.isEmpty()) {
            showToast("Middle name is required.")
        }
    }

    private fun validateLastName(lastName: String) {
        if (lastName.isEmpty()) {
            showToast("Last name is required.")
        }
    }

    private fun validateEmail(email: String) {
        if (!isValidEmail(email)) {
            showToast("Invalid email format. Please enter a valid email.")
        }
    }

    private fun validatePhone(phone: String) {
        if (!phone.startsWith("05")) {
            showToast("Phone number must start with 05.")
        }
    }

    private fun validatePassword(password: String) {
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters long, include at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character, with no spaces.", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String) {
        if (confirmPassword != password) {
            showToast("Passwords do not match. Please try again.")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        )
        return passwordPattern.matcher(password).matches()
    }

    private fun sendRegistrationRequest(fName: String, mName: String, lName: String, emailText: String, phone: String, pass: String) {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/register.php"
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("f_name", fName)
            .add("m_name", mName)
            .add("l_name", lName)
            .add("email", emailText)
            .add("phone_no", phone)
            .add("password", pass)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showToast("Registration failed: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        showToast("Registration successful")
                        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        showToast("Registration failed: ${response.body?.string()}")
                    }
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}