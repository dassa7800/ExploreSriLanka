package com.example.exploresrilanka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BookingsFragment : Fragment() {

    private lateinit var hotelAdapter: HotelAdapter
    private lateinit var hotelsList: MutableList<Hotel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bookings, container, false)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.hotels_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        hotelsList = mutableListOf()
        hotelAdapter = HotelAdapter(hotelsList)
        recyclerView.adapter = hotelAdapter

        // Fetch data from Firestore
        fetchHotels()

        return view
    }

    private fun fetchHotels() {
        firestore.collection("hotels")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val hotel = document.toObject(Hotel::class.java)
                    hotelsList.add(hotel)
                }
                hotelAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
                exception.printStackTrace()
            }
    }
}