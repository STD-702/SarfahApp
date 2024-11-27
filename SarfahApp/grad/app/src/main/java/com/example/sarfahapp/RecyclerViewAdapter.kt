package com.example.sarfahapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequestRecyclerViewAdapter(
    private val requests: List<Request>,
    private val onApprove: (Request) -> Unit
) : RecyclerView.Adapter<RequestRecyclerViewAdapter.RequestViewHolder>() {

    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val studentId: TextView = view.findViewById(R.id.studentId)
        val pickupTime: TextView = view.findViewById(R.id.pickupTime)
        val status: TextView = view.findViewById(R.id.status)
        val approveButton: Button = view.findViewById(R.id.approveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        holder.studentId.id = request.studentId
        holder.pickupTime.text = request.pickupTime
        holder.status.text = request.status
        holder.approveButton.setOnClickListener { onApprove(request) }
    }

    override fun getItemCount() = requests.size
}