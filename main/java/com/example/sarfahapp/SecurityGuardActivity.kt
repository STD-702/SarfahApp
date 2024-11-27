package com.example.sarfahapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class SecurityGuardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private var requests: MutableList<RequestData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_guard)

        setupRecyclerView()
        fetchApprovedRequests()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewRequests)
        recyclerView.layoutManager = LinearLayoutManager(this)

        requestAdapter = RequestAdapter(requests)
        recyclerView.adapter = requestAdapter
    }

    private fun fetchApprovedRequests() {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/get_security_requests.php"

        val stringRequest = object : StringRequest(Request.Method.GET, url,
            { response -> handleFetchResponse(response) },
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
                requests.add(request)
            }
            requestAdapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}