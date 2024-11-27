package com.example.sarfahapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CreateDelegateActivity : AppCompatActivity() {
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etDelegateId: EditText
    private lateinit var btnCreateDelegate: Button
    private lateinit var btnDeleteDelegate: Button
    private lateinit var btnLoadDelegates: Button
    private lateinit var recyclerView: RecyclerView
    private val client = OkHttpClient()
    private lateinit var delegateAdapter: DelegateAdapter
    private var parentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_delegate)

        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        parentId = sharedPreferences.getInt("parent_id", -1)

        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etLastName = findViewById(R.id.etLastName)
        etPhone = findViewById(R.id.etDelegatePhone)
        etPassword = findViewById(R.id.etPassword)
        etDelegateId = findViewById(R.id.etDelegateId)
        btnCreateDelegate = findViewById(R.id.btnCreateDelegate)
        btnDeleteDelegate = findViewById(R.id.btnDeleteDelegate)
        btnLoadDelegates = findViewById(R.id.btnLoadDelegates)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        delegateAdapter = DelegateAdapter { delegate -> editDelegate(delegate) }
        recyclerView.adapter = delegateAdapter

        btnCreateDelegate.setOnClickListener { createDelegate() }
        btnDeleteDelegate.setOnClickListener { deleteDelegate() }
        btnLoadDelegates.setOnClickListener { loadDelegates() }

        loadDelegates()
    }

    private fun loadDelegates() {
        if (parentId == -1) {
            Toast.makeText(this, "Invalid parent ID.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.3.65/dashboard/sarfah1.11/load_delegates.php"
        val formBody = FormBody.Builder()
            .add("parent_id", parentId.toString())
            .build()

        val request = okhttp3.Request.Builder().url(url).post(formBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CreateDelegateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                println("Load Delegates Response: $responseData") // Log the response for debugging

                try {
                    val jsonResponse = JSONObject(responseData)
                    runOnUiThread {
                        if (jsonResponse.getString("status") == "success") {
                            val delegates = jsonResponse.getJSONArray("delegates")
                            delegateAdapter.setDelegates(delegates)
                        } else {
                            Toast.makeText(this@CreateDelegateActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@CreateDelegateActivity, "JSON Parsing Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CreateDelegateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun editDelegate(delegate: JSONObject) {
        etFirstName.setText(delegate.getString("f_name"))
        etMiddleName.setText(delegate.getString("m_name"))
        etLastName.setText(delegate.getString("l_name"))
        etPhone.setText(delegate.getString("phone_no"))
        etDelegateId.setText(delegate.getString("delegate_id"))
    }

    private fun createDelegate() {
        val firstName = etFirstName.text.toString()
        val middleName = etMiddleName.text.toString()
        val lastName = etLastName.text.toString()
        val phone = etPhone.text.toString()
        val password = etPassword.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.3.65/dashboard/sarfah1.11/create_delegate.php"
        val formBody = FormBody.Builder()
            .add("f_name", firstName)
            .add("m_name", middleName)
            .add("l_name", lastName)
            .add("phone_no", phone)
            .add("password", password)
            .add("parent_id", parentId.toString())
            .build()

        val request = okhttp3.Request.Builder().url(url).post(formBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CreateDelegateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                println("Create Delegate Response: $responseData")

                try {
                    val jsonResponse = JSONObject(responseData)
                    runOnUiThread {
                        if (jsonResponse.getString("status") == "success") {
                            Toast.makeText(this@CreateDelegateActivity, "Delegate created successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@CreateDelegateActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@CreateDelegateActivity, "JSON Parsing Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CreateDelegateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun deleteDelegate() {
        val phoneNo = etDelegateId.text.toString()

        if (phoneNo.isEmpty()) {
            Toast.makeText(this, "Please enter a Delegate ID", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.3.65/dashboard/sarfah1.11/delete_delegate.php"
        val formBody = FormBody.Builder()
            .add("phone_no", phoneNo)
            .add("parent_id", parentId.toString())
            .build()

        val request = okhttp3.Request.Builder().url(url).post(formBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CreateDelegateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                println("Delete Delegate Response: $responseData")

                try {
                    val jsonResponse = JSONObject(responseData)
                    runOnUiThread {
                        if (jsonResponse.getString("status") == "success") {
                            Toast.makeText(this@CreateDelegateActivity, "Delegate deleted successfully!", Toast.LENGTH_SHORT).show()
                            etDelegateId.text.clear()
                        } else {
                            Toast.makeText(this@CreateDelegateActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@CreateDelegateActivity, "JSON Parsing Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CreateDelegateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}