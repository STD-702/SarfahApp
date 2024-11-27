package com.example.sarfahapp

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var requestButton: Button
    private lateinit var manageStudentsButton: Button
    private var selectedStudentId: Int? = null

    private val students = listOf(
        Pair(1, "com.example.sarfahapp.Student A"),
        Pair(2, "com.example.sarfahapp.Student B"),
        Pair(3, "com.example.sarfahapp.Student C")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeUI()
    }

    private fun initializeUI() {
        requestButton = findViewById(R.id.request_button)
        manageStudentsButton = findViewById(R.id.manage_students_button)

        requestButton.setOnClickListener { showStudentSelectionDialog() }
        manageStudentsButton.setOnClickListener { navigateToManageStudents() }
    }

    private fun showStudentSelectionDialog() {
        val studentNames = students.map { it.second }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select a com.example.sarfahapp.Student")
            .setItems(studentNames) { _, which ->
                selectedStudentId = students[which].first
                sendPickupRequest()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendPickupRequest() {
        val studentId = selectedStudentId ?: run {
            Toast.makeText(this, "No student selected", Toast.LENGTH_SHORT).show()
            return
        }
        val pickupTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val status = "pending"

        val url = "http://192.168.3.65/dashboard/sarfah1.11/send_request.php"
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("student_id", studentId.toString())
            .add("pickup_time", pickupTime)
            .add("status", status)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Toast.makeText(this@HomeActivity, "Request sent: $responseBody", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@HomeActivity, "Request failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun navigateToManageStudents() {
        // Implement navigation logic here
        Toast.makeText(this, "Navigating to Manage Students", Toast.LENGTH_SHORT).show()
    }
}
