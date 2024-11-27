package com.example.sarfahapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ShowRequestsActivity : AppCompatActivity() {
    private lateinit var rvRequests: RecyclerView
    private val client = OkHttpClient()
    private var parentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_requests)

        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        parentId = sharedPreferences.getInt("parent_id", -1)

        setupViews()
        fetchRequests()
    }

    private fun setupViews() {
        rvRequests = findViewById(R.id.rvRequests)
        rvRequests.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchRequests() {
        if (parentId == -1) {
            Toast.makeText(this, "Parent ID not found. Please log in again.", Toast.LENGTH_LONG).show()
            return
        }

        val url = "http://192.168.3.65/dashboard/sarfah1.11/get_requests.php?parent_id=$parentId"
        val request = okhttp3.Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ShowRequestsActivity, "Failed to load requests: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@ShowRequestsActivity, "Unexpected response: ${it.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val responseData = it.body?.string()
                    val jsonResponse = JSONObject(responseData)
                    runOnUiThread {
                        if (jsonResponse.getString("status") == "success") {
                            handleRequestData(jsonResponse.getJSONArray("requests"))
                        } else {
                            Toast.makeText(this@ShowRequestsActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun handleRequestData(requestsJsonArray: JSONArray) {
        val requests = mutableListOf<RequestData>()
        for (i in 0 until requestsJsonArray.length()) {
            val requestJson = requestsJsonArray.getJSONObject(i)
            val requestItem = RequestData(
                requestId = requestJson.getInt("request_id"),
                studentId = requestJson.getInt("student_id"),
                studentName = "${requestJson.getString("student_first_name")} ${requestJson.getString("student_middle_name")} ${requestJson.getString("student_last_name")}",
                pickupTime = requestJson.getString("pickup_time"),
                status = requestJson.getString("status")
            )
            requests.add(requestItem)
        }
        setupRequestAdapter(requests)
    }
    private fun setupRequestAdapter(requests: List<RequestData>) {
        val requestsAdapter = RequestAdapter(requests.toMutableList())
        rvRequests.adapter = requestsAdapter
    }
}