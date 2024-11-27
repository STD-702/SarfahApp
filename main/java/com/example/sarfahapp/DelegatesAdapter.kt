package com.example.sarfahapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DelegatesAdapter(
    private var delegates: MutableList<Delegate>,
    private val context: Context
) : RecyclerView.Adapter<DelegatesAdapter.DelegateViewHolder>(), Filterable {

    private var filteredDelegates: MutableList<Delegate> = delegates.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DelegateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_delegate, parent, false)
        return DelegateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DelegateViewHolder, position: Int) {
        val delegate = filteredDelegates[position]
        holder.bind(delegate)
    }

    override fun getItemCount(): Int {
        return filteredDelegates.size
    }

    inner class DelegateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvDelegateName)
        private val relationTextView: TextView = itemView.findViewById(R.id.tvDelegateRelation)

        fun bind(delegate: Delegate) {
            nameTextView.text = delegate.name
            relationTextView.text = delegate.relation
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    delegates
                } else {
                    delegates.filter {
                        it.name.contains(constraint, ignoreCase = true)
                    }.toMutableList()
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDelegates = results?.values as? MutableList<Delegate> ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }
}