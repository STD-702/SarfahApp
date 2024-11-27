package com.example.sarfahapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumber: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeUI()
    }

    private fun initializeUI() {
        phoneNumber = findViewById(R.id.phone_number)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        registerLink = findViewById(R.id.register_link)

        loginButton.setOnClickListener { login() }
        registerLink.setOnClickListener { navigateToRegister() }
    }

    private fun login() {
        val phone = phoneNumber.text.toString().trim()
        val pass = password.text.toString().trim()

        if (!phone.startsWith("05")) {
            Toast.makeText(this, "Phone number must start with 05", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.3.65/dashboard/sarfah1.11/login.php"
        val requestBody = FormBody.Builder()
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
                    Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response)
            }
        })
    }

    private fun handleResponse(response: Response) {
        val responseBody = response.body?.string()
        Log.d("LoginResponse", responseBody ?: "No response body")

        runOnUiThread {
            if (response.isSuccessful) {
                if (responseBody != null) {
                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val status = jsonResponse.getString("status")

                        if (status == "success") {
                            val userType = jsonResponse.getString("user_type")
                            val userId = when (userType) {
                                "parent" -> jsonResponse.getInt("parent_id")
                                "academic" -> jsonResponse.getInt("academic_id")
                                "delegated" -> jsonResponse.getInt("delegate_id")
                                "security_guards" -> jsonResponse.getInt("guard_id")
                                "teacher" -> jsonResponse.getInt("employee_id")
                                else -> null
                            }

                            userId?.let {
                                val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                                sharedPreferences.edit().putInt("${userType}_id", it).apply()
                            }

                            navigateToHome(userType)
                            finish()
                        } else {
                            val message = jsonResponse.getString("message")
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this@LoginActivity, "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "No response from server", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Login failed: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome(userType: String) {
        val intent = when (userType) {
            "academic" -> Intent(this, AcademicSupervisorActivity::class.java)
            "parent" -> Intent(this, ManageStudentsActivity::class.java)
            "delegated" -> Intent(this, DelegatedActivity::class.java)
            "security_guards" -> Intent(this, SecurityGuardActivity::class.java)
            "teacher" -> Intent(this, TeacherActivity::class.java)

            else -> null
        }
        intent?.let { startActivity(it) }
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }
}