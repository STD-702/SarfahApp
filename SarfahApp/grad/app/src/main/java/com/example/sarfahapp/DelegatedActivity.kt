package com.example.sarfahapp

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class DelegatedActivity : AppCompatActivity() {
    private lateinit var rvDelegates: RecyclerView
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
        checkParentIdAndFetchDelegates()
        setupBottomNavigation()
        checkUserLocation()
    }

    private fun setupViews() {
        rvDelegates = findViewById(R.id.rvDelegates)
        rvDelegates.layoutManager = LinearLayoutManager(this)
    }

    private fun checkParentIdAndFetchDelegates() {
        if (parentId != -1) {
            fetchParentDelegates()
        } else {
            Toast.makeText(this, "Parent ID not found. Please log in again.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
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

    private fun fetchParentDelegates() {
        val url = "http://172.20.10.4/dashboard/sarfah1.11/get_delegates.php?parent_id=$parentId"
        val request = okhttp3.Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@DelegatedActivity, "Failed to load delegates: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            handleDelegateData(jsonResponse.getJSONArray("delegates"))
                        } else {
                            Toast.makeText(this@DelegatedActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun handleDelegateData(delegatesJsonArray: JSONArray) {
        val delegates = mutableListOf<Delegate>()
        for (i in 0 until delegatesJsonArray.length()) {
            val delegateJson = delegatesJsonArray.getJSONObject(i)
            val delegate = Delegate(
                delegateJson.getInt("delegate_id"),
                delegateJson.getString("name"),
                delegateJson.getString("relation")
            )
            delegates.add(delegate)
        }
        setupDelegateAdapter(delegates)
    }

    private fun setupDelegateAdapter(delegates: List<Delegate>) {
        val delegatesAdapter = DelegatesAdapter(delegates.toMutableList(), this)
        rvDelegates.adapter = delegatesAdapter
    }


}