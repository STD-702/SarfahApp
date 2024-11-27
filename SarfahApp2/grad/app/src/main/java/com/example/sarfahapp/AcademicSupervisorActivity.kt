package com.example.sarfahapp

import android.content.Intent
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

class AcademicSupervisorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private var requests: MutableList<RequestData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_academic_supervisor)

        recyclerView = findViewById(R.id.recyclerViewRequests)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnCreateAcademic).setOnClickListener {
            startActivity(Intent(this, CreateTeacherActivity::class.java))
        }
        findViewById<Button>(R.id.btnAddStudents).setOnClickListener {
            startActivity(Intent(this, AddStudentsActivity::class.java))
        }
        findViewById<Button>(R.id.btnCreateGuard).setOnClickListener {
            startActivity(Intent(this, CreateGuardActivity::class.java))
        }

        // Initialize the adapter in manage mode
        requestAdapter = RequestAdapter(requests, true,
            { request -> approveRequest(request.requestId) },
            { request -> declineRequest(request.requestId) }
        )

        recyclerView.adapter = requestAdapter

        fetchRequests()
    }

    private fun fetchRequests() {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/get_requests.php"

        val stringRequest = object : StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    requests.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val request = RequestData(
                            jsonObject.getInt("request_id"),
                            jsonObject.getInt("student_id"),
                            "${jsonObject.getString("student_first_name")} ${jsonObject.getString("student_middle_name")} ${jsonObject.getString("student_last_name")}", // Add this line
                            jsonObject.optString("pickup_time"),
                            jsonObject.optString("status")
                        )
                        if (isPickupTimeInRange(request.pickupTime)) {
                            requests.add(request)
                        }
                    }
                    requestAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error fetching requests: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {}

        Volley.newRequestQueue(this).add(stringRequest)
    }
    private fun isPickupTimeInRange(pickupTime: String?): Boolean {
        if (pickupTime.isNullOrEmpty()) return false

        val timeParts = pickupTime.split(":")
        if (timeParts.size != 2) return false

        val hour = timeParts[0].toIntOrNull() ?: return false
        val minute = timeParts[1].toIntOrNull() ?: return false

        // Check if the hour and minute fall within the specified range
        return (hour == 7 && minute >= 0) || (hour in 8..11)
    }

    private fun approveRequest(requestId: Int) {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/approve_request.php"
        val params = HashMap<String, String>()
        params["request_id"] = requestId.toString()

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Log.d("ApproveRequest", "Response: $response")
                Toast.makeText(this, "Request approved", Toast.LENGTH_SHORT).show()
                fetchRequests()
            },
            { error ->
                Log.e("ApproveRequest", "Error: ${error.message}")
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
        val params = HashMap<String, String>()
        params["request_id"] = requestId.toString()

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Log.d("DeclineRequest", "Response: $response")
                Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show()
                fetchRequests()
            },
            { error ->
                Log.e("DeclineRequest", "Error: ${error.message}")
                Toast.makeText(this, "Error declining request: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}