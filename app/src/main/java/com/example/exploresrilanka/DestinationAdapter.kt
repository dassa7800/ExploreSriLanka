package com.example.exploresrilanka

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class DestinationAdapter(
    private val destinations: MutableList<Destination>,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

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

        // Set click listener on the itemView to open details
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DestinationDetailActivity::class.java)
            intent.putExtra("destination", currentDestination)
            holder.itemView.context.startActivity(intent)
        }

        // Show delete button for admin
        if (isAdmin) {
            holder.deleteButtonLayout.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(holder.itemView.context, currentDestination, position)
            }
        } else {
            holder.deleteButtonLayout.visibility = View.GONE
        }
    }

    override fun getItemCount() = destinations.size

    private fun showDeleteConfirmationDialog(context: Context, destination: Destination, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Destination")
            .setMessage("Are you sure you want to delete this destination?")
            .setPositiveButton("Yes") { _, _ ->
                deleteDestination(destination, position, context)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteDestination(destination: Destination, position: Int, context: Context) {
        val db = FirebaseFirestore.getInstance()
        db.collection("destinations").document(destination.id)
            .delete()
            .addOnSuccessListener {
                destinations.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, destinations.size)
                Toast.makeText(context, "Destination deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to delete destination: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destinationImage: ImageView = itemView.findViewById(R.id.destination_image)
        val destinationName: TextView = itemView.findViewById(R.id.destination_name)
        val destinationCategory: TextView = itemView.findViewById(R.id.destination_category)
        val deleteButtonLayout: LinearLayout = itemView.findViewById(R.id.delete_button_layout)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
}