package com.example.sarfahapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class TeacherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private var requests: MutableList<RequestData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        setupRecyclerView()

        findViewById<Button>(R.id.btnSelectClass).setOnClickListener {
            // Logic to select class (show a dialog or new activity)
            // After selection, call fetchRequests()
            fetchRequests()
        }

        fetchRequests()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewRequests)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        requestAdapter = RequestAdapter(requests, true,
            { request -> approveRequest(request.requestId) },
            { request -> declineRequest(request.requestId) }
        )
        recyclerView.adapter = requestAdapter
    }

    private fun fetchRequests() {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/get_teacher_requests.php?employee_id=1&class=1"

        val stringRequest = object : StringRequest(Request.Method.GET, url,
            { response ->
                handleFetchResponse(response)
            },
            { error ->
                Toast.makeText(this, "Error fetching requests: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {}

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun handleFetchResponse(response: String) {
        try {
            val jsonArray = JSONArray(response)
            requests.clear()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val request = RequestData(
                    requestId = jsonObject.getInt("request_id"),
                    studentId = jsonObject.getInt("student_id"),
                    studentName = "${jsonObject.getString("student_first_name")} ${jsonObject.getString("student_middle_name")} ${jsonObject.getString("student_last_name")}",
                    pickupTime = jsonObject.optString("pickup_time"),
                    status = jsonObject.optString("status")
                )
                if (isWithinApprovalTime()) {
                    requests.add(request)
                }
            }
            requestAdapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isWithinApprovalTime(): Boolean {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return currentHour in 12..16
    }

    private fun approveRequest(requestId: Int) {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/approve_request.php"
        val params = HashMap<String, String>().apply {
            put("request_id", requestId.toString())
        }

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(this, "Request approved", Toast.LENGTH_SHORT).show()
                fetchRequests()
            },
            { error ->
                Toast.makeText(this, "Error approving request: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun declineRequest(requestId: Int) {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/decline_request.php"
        val params = HashMap<String, String>().apply {
            put("request_id", requestId.toString())
        }

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show()
                fetchRequests()
            },
            { error ->
                Toast.makeText(this, "Error declining request: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}