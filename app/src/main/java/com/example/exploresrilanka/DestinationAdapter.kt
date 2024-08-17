package com.example.exploresrilanka

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DestinationAdapter(private val destinations: List<Destination>) : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_destination, parent, false)
        return DestinationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val currentDestination = destinations[position]
        holder.destinationName.text = currentDestination.name
        holder.destinationCategory.text = "Category: ${currentDestination.category}"

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(currentDestination.image)
            .into(holder.destinationImage)

        // Set click listener on the itemView
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DestinationDetailActivity::class.java)
            intent.putExtra("destination", currentDestination)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount() = destinations.size

    class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destinationImage: ImageView = itemView.findViewById(R.id.destination_image)
        val destinationName: TextView = itemView.findViewById(R.id.destination_name)
        val destinationCategory: TextView = itemView.findViewById(R.id.destination_category)
    }
}