package com.example.sarfahapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        val studentsRecyclerView = findViewById<RecyclerView>(R.id.recyclerViewStudents)
        studentsRecyclerView.layoutManager = LinearLayoutManager(this)
        studentsRecyclerView.adapter = StudentsAdapter(getStudents())
    }

    private fun getStudents(): List<Student> {
        return listOf(
            Student("Norah", "2nd Grade", "21st School"),
            Student("Mohammed", "5th Grade", "AlFursan School"),
            Student("Saud", "8th Grade", "Suyuti Educational Complex"),
            Student("Abdullah", "12th Grade", "Suyuti Educational Complex")
        )
    }
}
