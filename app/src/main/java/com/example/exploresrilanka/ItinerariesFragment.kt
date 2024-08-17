package com.example.exploresrilanka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ItinerariesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var itinerariesRecyclerView: RecyclerView
    private lateinit var itinerariesAdapter: ItinerariesAdapter
    private val itinerariesList = mutableListOf<Itinerary>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_itineraries, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Setup RecyclerView
        itinerariesRecyclerView = view.findViewById(R.id.itineraries_recycler_view)
        itinerariesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itinerariesAdapter = ItinerariesAdapter(itinerariesList)
        itinerariesRecyclerView.adapter = itinerariesAdapter

        // Load itineraries
        loadItineraries()

        return view
    }

    private fun loadItineraries() {
        val userEmail = auth.currentUser?.email ?: return

        firestore.collection("itineraries")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val itinerary = document.toObject(Itinerary::class.java)
                    itinerariesList.add(itinerary)
                }
                itinerariesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error getting itineraries: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

data class Itinerary(
    val userEmail: String = "",
    val userName: String = "",
    val itinerary: String = ""
)