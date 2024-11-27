package com.example.sarfahapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btnStudents).setOnClickListener {
            startActivity(Intent(this, StudentsActivity::class.java))
        }

        findViewById<Button>(R.id.btnDelegates).setOnClickListener {
            startActivity(Intent(this, DelegatesActivity::class.java))
        }

        findViewById<Button>(R.id.btnRequests).setOnClickListener {
            startActivity(Intent(this, ShowRequestsActivity::class.java))
        }
    }
}
