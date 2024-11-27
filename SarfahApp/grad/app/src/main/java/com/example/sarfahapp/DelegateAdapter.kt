package com.example.sarfahapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DelegateAdapter(private val onClick: (JSONObject) -> Unit) : RecyclerView.Adapter<DelegateAdapter.ViewHolder>() {
    private var delegates = JSONArray()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tvDelegateName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delegate_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val delegate = delegates.getJSONObject(position)
            holder.nameTextView.text = "${delegate.getString("f_name")} ${delegate.getString("l_name")}"

            holder.itemView.setOnClickListener { onClick(delegate) }
        } catch (e: JSONException) {
            holder.nameTextView.text = "Error loading name"
        }
    }

    override fun getItemCount(): Int = delegates.length()

    fun setDelegates(newDelegates: JSONArray) {
        delegates = newDelegates
        notifyDataSetChanged()
    }
}