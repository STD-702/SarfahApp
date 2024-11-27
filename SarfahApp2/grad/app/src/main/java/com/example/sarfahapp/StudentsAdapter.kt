package com.example.sarfahapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentsAdapter(
    private var students: MutableList<Student>,
    private val clickListener: (Student) -> Unit
) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    private var filteredStudents: MutableList<Student> = students.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_students, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = filteredStudents[position]
        holder.bind(student, clickListener)
    }

    override fun getItemCount(): Int = filteredStudents.size

    fun filter(query: String?) {
        filteredStudents.clear()
        if (query.isNullOrEmpty()) {
            filteredStudents.addAll(students)
        } else {
            val lowerCaseQuery = query.lowercase()
            students.forEach { student ->
                if (student.firstName.lowercase().contains(lowerCaseQuery) ||
                    student.lastName.lowercase().contains(lowerCaseQuery)) {
                    filteredStudents.add(student)
                }
            }
        }
        notifyDataSetChanged()
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstNameTextView: TextView = itemView.findViewById(R.id.tvFirstName)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.tvLastName)

        fun bind(student: Student, clickListener: (Student) -> Unit) {
            firstNameTextView.text = student.firstName
            lastNameTextView.text = student.lastName

            itemView.setOnClickListener { clickListener(student) }
        }
    }
}