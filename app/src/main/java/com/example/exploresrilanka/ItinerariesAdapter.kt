package com.example.exploresrilanka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItinerariesAdapter(
    private val itineraries: List<Itinerary>,
    private val onItemClick: (Itinerary) -> Unit // Click listener to handle item clicks
) : RecyclerView.Adapter<ItinerariesAdapter.ItineraryCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itinerary_card, parent, false)
        return ItineraryCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItineraryCardViewHolder, position: Int) {
        val itinerary = itineraries[position]
        holder.bind(itinerary)
        holder.itemView.setOnClickListener { onItemClick(itinerary) } // Set click listener
    }

    override fun getItemCount(): Int = itineraries.size

    class ItineraryCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itineraryIdTextView: TextView = itemView.findViewById(R.id.card_itinerary_id)
        private val userNameTextView: TextView = itemView.findViewById(R.id.card_itinerary_user)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.card_itinerary_created_at)

        fun bind(itinerary: Itinerary) {
            itineraryIdTextView.text = "Itinerary ${itinerary.documentId}"
            userNameTextView.text = "Created by: ${itinerary.userName}"
            createdAtTextView.text = "Created At: ${formatTimestamp(itinerary.createdAt)}"
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}