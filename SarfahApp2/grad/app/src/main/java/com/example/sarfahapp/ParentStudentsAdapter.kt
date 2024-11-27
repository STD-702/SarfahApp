package com.example.sarfahapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ParentStudentsAdapter(
    private var students: MutableList<Student>,
    private val context: Context,
    private val onRequestPickup: (Student) -> Unit,
    private val onRequestEarlyLeave: (Student) -> Unit
) : RecyclerView.Adapter<ParentStudentsAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val btnRequestPickup: TextView = itemView.findViewById(R.id.btnRequestPickup)
        private val btnRequestEarlyLeave: TextView = itemView.findViewById(R.id.btnEarlyLeave)

        fun bind(student: Student) {
            tvStudentName.text = formatStudentName(student)

            btnRequestPickup.setOnClickListener {
                onRequestPickup(student)
            }

            btnRequestEarlyLeave.setOnClickListener {
                onRequestEarlyLeave(student)
            }
        }

        private fun formatStudentName(student: Student): String {
            return "${student.firstName} ${student.middleName ?: ""} ${student.lastName}".trim()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    fun updateStudents(newStudents: List<Student>) {
        students.clear()
        students.addAll(newStudents)
        notifyDataSetChanged()
    }

    fun filter(query: String?) {
        val filteredStudents = if (query.isNullOrEmpty()) {
            students
        } else {
            students.filter {
                it.firstName.contains(query, ignoreCase = true) ||
                        it.middleName?.contains(query, ignoreCase = true) == true ||
                        it.lastName.contains(query, ignoreCase = true)
            }
        }
        updateStudents(filteredStudents)
    }
}