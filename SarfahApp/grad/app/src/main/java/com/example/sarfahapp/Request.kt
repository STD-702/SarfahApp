package com.example.sarfahapp

data class Request(
    val requestId: Int,
    val studentId: Int,
    val studentName: String,
    val pickupTime: String,
    val status: String
) {
    class Builder {
        private var requestId: Int = 0
        private var studentId: Int = 0
        private var studentName: String = ""
        private var pickupTime: String = ""
        private var status: String = ""

        fun setRequestId(id: Int) = apply { this.requestId = id }
        fun setStudentId(id: Int) = apply { this.studentId = id }
        fun setStudentName(name: String) = apply { this.studentName = name }
        fun setPickupTime(time: String) = apply { this.pickupTime = time }
        fun setStatus(status: String) = apply { this.status = status }

        fun build(): Request {
            return Request(requestId, studentId, studentName, pickupTime, status)
        }
    }
}