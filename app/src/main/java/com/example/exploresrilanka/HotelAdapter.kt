package com.example.exploresrilanka

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class HotelAdapter(
    private val hotels: MutableList<Hotel>,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return HotelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val currentHotel = hotels[position]
        holder.hotelName.text = currentHotel.name
        holder.hotelDescription.text = currentHotel.description
        holder.hotelRating.text = "Rating: ${currentHotel.rating}"

        Glide.with(holder.itemView.context)
            .load(currentHotel.image)
            .into(holder.hotelImage)

        if (isAdmin) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(currentHotel, position, holder)
            }
        } else {
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = hotels.size

    private fun showDeleteConfirmationDialog(hotel: Hotel, position: Int, holder: HotelViewHolder) {
        val context = holder.itemView.context
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Hotel")
        builder.setMessage("Are you sure you want to delete ${hotel.name}?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteHotel(hotel, position, holder)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun deleteHotel(hotel: Hotel, position: Int, holder: HotelViewHolder) {
        val db = FirebaseFirestore.getInstance()
        db.collection("hotels").document(hotel.id)
            .delete()
            .addOnSuccessListener {
                hotels.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, hotels.size)
                Toast.makeText(holder.itemView.context, "Hotel deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(holder.itemView.context, "Failed to delete hotel: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotelImage: ImageView = itemView.findViewById(R.id.hotel_image)
        val hotelName: TextView = itemView.findViewById(R.id.hotel_name)
        val hotelDescription: TextView = itemView.findViewById(R.id.hotel_description)
        val hotelRating: TextView = itemView.findViewById(R.id.hotel_rating)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
}