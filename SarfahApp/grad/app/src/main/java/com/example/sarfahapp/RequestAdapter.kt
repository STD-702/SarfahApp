package com.example.sarfahapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequestAdapter(
    private val requests: MutableList<RequestData>,
    private val isManageMode: Boolean = false,
    private val onApproveClick: ((RequestData) -> Unit)? = null,
    private val onDeclineClick: ((RequestData) -> Unit)? = null
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.studentName)
        val pickupTime: TextView = itemView.findViewById(R.id.pickupTime)
        val status: TextView = itemView.findViewById(R.id.status)
        val approveButton: Button = itemView.findViewById(R.id.approveButton)
        val declineButton: Button = itemView.findViewById(R.id.declineButton)

        init {
            if (isManageMode) {
                approveButton.setOnClickListener {
                    onApproveClick?.invoke(requests[adapterPosition])
                }
                declineButton.setOnClickListener {
                    onDeclineClick?.invoke(requests[adapterPosition])
                }
            } else {
                approveButton.visibility = View.GONE
                declineButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.studentName.text = requests[position].studentName // Update this line
        holder.pickupTime.text = requests[position].pickupTime
        holder.status.text = requests[position].status
    }

    override fun getItemCount(): Int = requests.size

    fun updateRequests(newRequests: List<RequestData>) {
        requests.clear()
        requests.addAll(newRequests)
        notifyDataSetChanged()
    }
}