package com.example.sarfahapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val textView = findViewById<TextView>(R.id.tvAboutUs)
        textView.text = "The goal of this app is to make picking up your children easier and we are trying to reduce the traffic in front of the school."
    }
}