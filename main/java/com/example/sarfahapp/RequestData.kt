package com.example.sarfahapp

data class RequestData(
    val requestId: Int,
    val studentId: Int,
    val studentName: String,
    val pickupTime: String,
    val status: String
)