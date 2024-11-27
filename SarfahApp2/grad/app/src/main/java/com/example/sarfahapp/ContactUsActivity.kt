package com.example.sarfahapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class ContactUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        val textView = findViewById<TextView>(R.id.tvContactUs)
        textView.text = "رقم التواصل"
        textView.text = "0559550089"
    }
}