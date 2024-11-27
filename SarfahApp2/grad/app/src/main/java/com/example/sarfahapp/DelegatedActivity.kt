package com.example.sarfahapp

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DelegatedActivity : AppCompatActivity() {
    private lateinit var rvStudents: RecyclerView
    private lateinit var searchView: SearchView
    private val client = OkHttpClient()
    private var parentId: Int = -1
    private lateinit var locationManager: LocationManager
    private var isInSchoolPremises: Boolean = false

    private val schoolLatitude = 37.4219524
    private val schoolLongitude = -122.0840679
    private val schoolRadius = 10000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delegated)

        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        parentId = sharedPreferences.getInt("parent_id", -1)

        setupViews()
        checkParentIdAndFetchStudents()
        setupSearchView()
        setupBottomNavigation()
        checkUserLocation()
    }

    private fun setupViews() {
        rvStudents = findViewById(R.id.rvStudents)
        searchView = findViewById(R.id.searchView)

        rvStudents.layoutManager = LinearLayoutManager(this)
    }

    private fun checkParentIdAndFetchStudents() {
        if (parentId != -1) {
            fetchParentStudents()
        } else {
            Toast.makeText(this, "Parent ID not found. Please log in again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterStudents(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterStudents(newText)
                return true
            }
        })
    }

    private fun setupBottomNavigation() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutUsActivity::class.java))
                    true
                }
                R.id.nav_contact -> {
                    startActivity(Intent(this, ContactUsActivity::class.java))
                    true
                }
                R.id.nav_show_requests -> {
                    startActivity(Intent(this, ShowRequestsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun checkUserLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                checkIfInSchoolPremises(location)
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })
    }

    private fun checkIfInSchoolPremises(location: Location) {
        val schoolLocation = Location("").apply {
            latitude = schoolLatitude
            longitude = schoolLongitude
        }

        isInSchoolPremises = location.distanceTo(schoolLocation) <= schoolRadius

        val message = if (isInSchoolPremises) {
            "You are within the school premises."
        } else {
            "You are NOT within the school premises."
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun fetchParentStudents() {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/get_students.php?parent_id=$parentId"
        val request = okhttp3.Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@DelegatedActivity, "Failed to load students: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@DelegatedActivity, "Unexpected response: ${it.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val responseData = it.body?.string()
                    val jsonResponse = JSONObject(responseData)
                    runOnUiThread {
                        if (jsonResponse.getString("status") == "success") {
                            handleStudentData(jsonResponse.getJSONArray("students"))
                        } else {
                            Toast.makeText(this@DelegatedActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun handleStudentData(studentsJsonArray: JSONArray) {
        val students = mutableListOf<Student>()
        for (i in 0 until studentsJsonArray.length()) {
            val studentJson = studentsJsonArray.getJSONObject(i)
            val student = Student(
                studentJson.getInt("student_id"),
                studentJson.getString("f_name"),
                studentJson.optString("m_name"),
                studentJson.getString("l_name")
            )
            students.add(student)
        }
        setupStudentAdapter(students)
    }

    private fun setupStudentAdapter(students: List<Student>) {
        val studentsAdapter = StudentsAdapter(students.toMutableList()) { student ->
            requestPickup(student)
        }
        rvStudents.adapter = studentsAdapter
    }

    private fun filterStudents(query: String?) {
        (rvStudents.adapter as? StudentsAdapter)?.filter(query)
    }

    private fun requestPickup(student: Student) {
        if (!isInSchoolPremises) {
            Toast.makeText(this, "You must be within the school premises to send a pickup request.", Toast.LENGTH_LONG).show()
            return
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        if (currentHour < 12 || currentHour >= 23) {
            Toast.makeText(this, "Pickup requests can only be made between 12 PM and 4 PM.", Toast.LENGTH_LONG).show()
            return
        }

        showPickupRequestDialog(student)
    }

    private fun showPickupRequestDialog(student: Student) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Request Pickup for ${student.firstName} ${student.lastName}")

        val view = layoutInflater.inflate(R.layout.dialog_request_pickup, null)
        builder.setView(view)

        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val tvCurrentTime = view.findViewById<TextView>(R.id.tvCurrentTime)
        tvCurrentTime.text = currentTime

        builder.setPositiveButton("Send Request") { _, _ ->
            sendPickupRequest(student.studentId, currentTime)
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun sendPickupRequest(studentId: Int, pickupTime: String) {
        val url = "http://192.168.3.65/dashboard/sarfah1.11/send_request.php"
        val formBody = FormBody.Builder()
            .add("student_id", studentId.toString())
            .add("pickup_time", pickupTime)
            .add("status", "pending")
            .build()

        val request = okhttp3.Request.Builder().url(url).post(formBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@DelegatedActivity, "Failed to send request: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseData = it.body?.string()
                    val jsonResponse = JSONObject(responseData)
                    runOnUiThread {
                        Toast.makeText(this@DelegatedActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}