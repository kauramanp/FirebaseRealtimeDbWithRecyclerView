package com.example.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(var recyclerDataClass: ArrayList<RecyclerDataClass>, var recyclerInterface: RecyclerInterface) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTitle: TextView = view.findViewById(R.id.tvTitle)
        var tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_recycler, parent, false);
        return ViewHolder(view.rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = recyclerDataClass[position].title
        holder.tvMessage.text = recyclerDataClass[position].message
        holder.itemView.setOnClickListener {
            recyclerInterface.recyclerInterface(recyclerDataClass[position])
        }
    }

    override fun getItemCount(): Int {
        return recyclerDataClass.size
    }
}