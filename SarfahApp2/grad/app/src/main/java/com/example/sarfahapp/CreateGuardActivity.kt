package com.example.sarfahapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class CreateGuardActivity : AppCompatActivity() {

    private lateinit var editTextPhoneNo: EditText
    private lateinit var editTextFName: EditText
    private lateinit var editTextMName: EditText
    private lateinit var editTextLName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_guard)

        editTextPhoneNo = findViewById(R.id.editTextPhoneNo)
        editTextFName = findViewById(R.id.editTextFName)
        editTextMName = findViewById(R.id.editTextMName)
        editTextLName = findViewById(R.id.editTextLName)
        editTextPassword = findViewById(R.id.editTextPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            createGuard()
        }
    }

    private fun createGuard() {
        val phoneNo = editTextPhoneNo.text.toString().trim()
        val fName = editTextFName.text.toString().trim()
        val mName = editTextMName.text.toString().trim()
        val lName = editTextLName.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        val url = "http://192.168.3.65/dashboard/sarfah1.11/create_guard.php"
        val params = HashMap<String, String>()
        params["phone_no"] = phoneNo
        params["f_name"] = fName
        params["m_name"] = mName
        params["l_name"] = lName
        params["password"] = password

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->

                Toast.makeText(this, "Guard account created successfully", Toast.LENGTH_LONG).show()
                finish()
            },
            { error ->

                Toast.makeText(this, "Error creating guard: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}