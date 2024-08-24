package com.example.exploresrilanka

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminBookingsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var hotelRecyclerView: RecyclerView
    private lateinit var hotelAdapter: HotelAdapter
    private val hotelList = mutableListOf<Hotel>()
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_bookings, container, false)

        firestore = FirebaseFirestore.getInstance()
        hotelRecyclerView = view.findViewById(R.id.hotel_recycler_view)
        hotelRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        hotelAdapter = HotelAdapter(hotelList, isAdmin)
        hotelRecyclerView.adapter = hotelAdapter

        checkIfAdmin { isAdmin ->
            this.isAdmin = isAdmin
            hotelAdapter = HotelAdapter(hotelList, isAdmin)
            hotelRecyclerView.adapter = hotelAdapter
            loadHotelsFromFirebase()
        }

        val addHotelFab: FloatingActionButton = view.findViewById(R.id.add_hotel_fab)
        addHotelFab.setOnClickListener {
            showAddHotelDialog()
        }

        return view
    }

    private fun checkIfAdmin(callback: (Boolean) -> Unit) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        firestore.collection("users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val role = document.getString("role")
                    callback(role == "admin")
                    return@addOnSuccessListener
                }
                callback(false)
            }
            .addOnFailureListener {
                callback(false)
                Toast.makeText(context, "Error checking user role: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadHotelsFromFirebase() {
        firestore.collection("hotels")
            .get()
            .addOnSuccessListener { result ->
                hotelList.clear()
                for (document in result) {
                    val hotel = document.toObject(Hotel::class.java)
                    hotelList.add(hotel)
                }
                hotelAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error getting hotels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddHotelDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_hotel, null)
        builder.setView(dialogView)

        val nameInput = dialogView.findViewById<EditText>(R.id.hotel_name_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.hotel_description_input)
        val imageInput = dialogView.findViewById<EditText>(R.id.hotel_image_input)
        val ratingInput = dialogView.findViewById<EditText>(R.id.hotel_rating_input)

        builder.setTitle("Add New Hotel")
        builder.setPositiveButton("Add") { dialog, _ ->
            val name = nameInput.text.toString()
            val description = descriptionInput.text.toString()
            val image = imageInput.text.toString()
            val rating = ratingInput.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && image.isNotEmpty() && rating.isNotEmpty()) {
                val newHotel = Hotel(
                    id = firestore.collection("hotels").document().id,
                    name = name,
                    description = description,
                    image = image,
                    rating = rating
                )

                firestore.collection("hotels").document(newHotel.id)
                    .set(newHotel)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Hotel added successfully", Toast.LENGTH_SHORT).show()
                        hotelList.add(newHotel)
                        hotelAdapter.notifyItemInserted(hotelList.size - 1)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to add hotel: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}