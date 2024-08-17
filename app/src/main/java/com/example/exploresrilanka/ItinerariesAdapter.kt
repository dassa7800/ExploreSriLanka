package com.example.exploresrilanka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItinerariesAdapter(private val itineraries: List<Itinerary>) :
    RecyclerView.Adapter<ItinerariesAdapter.ItineraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary, parent, false)
        return ItineraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        val itinerary = itineraries[position]
        holder.itineraryTextView.text = itinerary.itinerary
        holder.userNameTextView.text = "Created by: ${itinerary.userName}"
    }

    override fun getItemCount(): Int = itineraries.size

    class ItineraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itineraryTextView: TextView = itemView.findViewById(R.id.itinerary_text)
        val userNameTextView: TextView = itemView.findViewById(R.id.user_name_text)
    }
}