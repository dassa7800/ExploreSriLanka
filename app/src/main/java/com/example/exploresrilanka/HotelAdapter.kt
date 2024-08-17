package com.example.exploresrilanka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HotelAdapter(private val hotels: List<Hotel>) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return HotelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val currentHotel = hotels[position]
        holder.hotelName.text = currentHotel.name
        holder.hotelDescription.text = currentHotel.description
        holder.hotelRating.text = "Rating: ${currentHotel.rating}"

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(currentHotel.image)
            .into(holder.hotelImage)
    }

    override fun getItemCount() = hotels.size

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotelImage: ImageView = itemView.findViewById(R.id.hotel_image)
        val hotelName: TextView = itemView.findViewById(R.id.hotel_name)
        val hotelDescription: TextView = itemView.findViewById(R.id.hotel_description)
        val hotelRating: TextView = itemView.findViewById(R.id.hotel_rating)
    }
}