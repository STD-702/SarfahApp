package com.example.sarfahapp

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ManageRequestsActivity : AppCompatActivity() {
    private lateinit var rvRequests: RecyclerView
    private lateinit var requestsAdapter: RequestAdapter
    private val client = OkHttpClient()
    private var parentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_requests)

        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        parentId = sharedPreferences.getInt("parent_id", -1)

        setupViews()
        checkParentIdAndFetchRequests()
    }

    private fun setupViews() {
        rvRequests = findViewById(R.id.rvRequests)
        rvRequests.layoutManager = LinearLayoutManager(this)
        requestsAdapter = RequestAdapter(mutableListOf(), isManageMode = true) // Set isManageMode to true
        rvRequests.adapter = requestsAdapter
    }

    private fun checkParentIdAndFetchRequests() {
        if (parentId != -1) {
            fetchPickupRequests()
        } else {
            Toast.makeText(this, "Parent ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchPickupRequests() {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/get_pickup_requests.php?parent_id=$parentId"
        val request = okhttp3.Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FetchPickupRequests", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@ManageRequestsActivity, "Failed to load requests: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("FetchPickupRequests", "Response Data: $responseData")
                try {
                    val jsonArray = JSONArray(responseData)
                    runOnUiThread {
                        handlePickupRequestData(jsonArray)
                    }
                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@ManageRequestsActivity, "JSON parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun handlePickupRequestData(requestsJsonArray: JSONArray) {
        val requests = mutableListOf<RequestData>()
        for (i in 0 until requestsJsonArray.length()) {
            val requestJson = requestsJsonArray.getJSONObject(i)
            val request = RequestData(
                requestId = requestJson.getInt("request_id"),
                studentId = requestJson.getInt("student_id"),
                studentName = "${requestJson.getString("student_first_name")} ${requestJson.getString("student_middle_name")} ${requestJson.getString("student_last_name")}", // Include student name
                pickupTime = requestJson.getString("pickup_time"),
                status = requestJson.getString("status")
            )
            requests.add(request)
        }
        requestsAdapter.updateRequests(requests)
    }
}